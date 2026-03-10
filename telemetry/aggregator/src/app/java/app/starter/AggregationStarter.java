package app.java.app.starter;

import jakarta.annotation.PreDestroy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import app.java.app.service.AggregatorService;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.errors.WakeupException;
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

    private volatile boolean running = true;

    @Value("${kafka.values.timeout}")
    private int consumeTimeout;

    @Value("${kafka.topics.sensor}")
    private String sensorTopic;

    @Value("${kafka.topics.snapshot}")
    private String snapshotTopic;

    public void start() {
        eventConsumer.subscribe(List.of(sensorTopic));

        try {
            while (running) {
                ConsumerRecords<String, SensorEventAvro> records =
                        eventConsumer.poll(Duration.ofMillis(consumeTimeout));

                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    Optional<SensorsSnapshotAvro> sensor = service.updateState(record.value());

                    sensor.ifPresent(sensorsSnapshotAvro ->
                            snapshotProducer.send(new ProducerRecord<>(snapshotTopic, null, sensorsSnapshotAvro)));
                }
            }

        } catch (WakeupException e) {

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void stop() {
        running = false;
        eventConsumer.wakeup();
    }

    @PreDestroy
    public void cleanup() {
        stop();

        if (eventConsumer != null) eventConsumer.close();
        if (snapshotProducer != null) snapshotProducer.close();
    }
}
