package main.java.app.service;

import lombok.RequiredArgsConstructor;

import org.apache.avro.specific.SpecificRecordBase;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.time.Instant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AggregatorService {
    private final Consumer<String, SpecificRecordBase> consumer;

    @Value("${kafka.topics.snapshot}")
    private String snapshotTopic;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        consumer.subscribe(List.of(snapshotTopic));

        ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
        SensorsSnapshotAvro snapshot = null;

        for (ConsumerRecord<String, SpecificRecordBase> record : records) {
            if (record.value().get(1).equals(event.get(1))) {
                snapshot = (SensorsSnapshotAvro) record.value();

                if (snapshot.getSensorsState().containsKey(event.getId())
                        && snapshot.getSensorsState().get(event.getId()).getTimestamp().isBefore(event.getTimestamp())) {
                    snapshot.getSensorsState().put(event.getId(),
                            SensorStateAvro.newBuilder()
                                .setTimestamp(event.getTimestamp())
                                .setData( event.getPayload()).build());
                } else {
                    consumer.close();

                    return Optional.empty();
                }
            } else {
                snapshot = SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant.now())
                        .setSensorsState(Map.of(event.getId(),SensorStateAvro.newBuilder()
                                .setTimestamp(event.getTimestamp())
                                .setData( event.getPayload()).build())).build();
            }
        }

        consumer.close();

        return Optional.of(snapshot);
    }
}
