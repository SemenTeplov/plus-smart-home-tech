package app.java.app.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import app.java.app.service.AggregatorService;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final AggregatorService service;

    private final Consumer<String, SensorEventAvro> eventConsumer;

    private final Producer<String, SensorsSnapshotAvro> snapshotProducer;

    private static final int CONSUME_ATTEMPT_TIMEOUT = 100;

    @Value("${kafka.topics.sensor}")
    private String sensorTopic;

    @Value("${kafka.topics.snapshot}")
    private String snapshotTopic;

    public void start() {
        try {
            eventConsumer.subscribe(List.of(sensorTopic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records =
                        eventConsumer.poll(Duration.ofMillis(CONSUME_ATTEMPT_TIMEOUT));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    Optional<SensorsSnapshotAvro> sensor = service.updateState(record.value());

                    sensor.ifPresent(sensorsSnapshotAvro ->
                            snapshotProducer.send(new ProducerRecord<>(snapshotTopic, null, sensorsSnapshotAvro)));
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (eventConsumer != null) {
                try {
                    eventConsumer.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }

            if (snapshotProducer != null) {
                try {
                    snapshotProducer.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}
