package app.java.app.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import app.java.app.service.AggregatorService;

import org.apache.avro.specific.SpecificRecordBase;
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

    private final Consumer<String, SpecificRecordBase> consumer;

    private final Producer<String, SpecificRecordBase> producer;

    @Value("${kafka.topics.snapshot}")
    private String snapshotTopic;

    @Value("${kafka.topics.sensor}")
    private String sensorTopic;

    public void start() {
        try {
            consumer.subscribe(List.of(sensorTopic));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    Optional<SensorsSnapshotAvro> sensor = service.updateState((SensorEventAvro) record.value());

                    sensor.ifPresent(sensorsSnapshotAvro ->
                            producer.send(new ProducerRecord<>(snapshotTopic, null, sensorsSnapshotAvro)));
                }
            }

        } catch (WakeupException ignored) {

            log.error(ignored.getMessage());

        } catch (Exception e) {

            log.error(e.getMessage());

        } finally {
            try {

                producer.flush();
                consumer.commitSync();

            } finally {

                consumer.close();
                producer.close();

            }
        }
    }
}
