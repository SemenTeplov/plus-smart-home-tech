package app.java.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorService {
    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;

    @Value("${app.consume.timeout:100}")
    private int consumeTimeout;

    @Value("${kafka.topics.snapshot}")
    private String snapshotTopic;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        snapshotConsumer.subscribe(List.of(snapshotTopic));

        try {
            SensorsSnapshotAvro snapshot = getSensorsSnapshots(event);

            if (!shouldUpdate(snapshot, event)) {
                return Optional.empty();
            }

            return Optional.of(createSensorsSnapshot(snapshot, event));

        } catch (Exception e) {
            log.error(e.getMessage());

            return Optional.empty();
        }
    }

    private SensorsSnapshotAvro getSensorsSnapshots(SensorEventAvro event) {
        ConsumerRecords<String, SensorsSnapshotAvro> records =
                snapshotConsumer.poll(Duration.ofMillis(consumeTimeout));

        SensorsSnapshotAvro snapshot = SensorsSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setSensorsState(Map.of(event.getId(), SensorStateAvro.newBuilder()
                        .setTimestamp(event.getTimestamp())
                        .setData(event.getPayload()).build()))
                .build();

        for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
            if (record.value().getHubId().equals(event.getHubId())) {
                snapshot = record.value();
            }
        }

        return snapshot;
    }

    private boolean shouldUpdate(SensorsSnapshotAvro snapshot, SensorEventAvro event) {
        if (!snapshot.getSensorsState().containsKey(event.getId())) {
            return true;
        }

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

        return oldState.getTimestamp().isBefore(event.getTimestamp())
                || oldState.getData().equals(event.getPayload());
    }

    private SensorsSnapshotAvro createSensorsSnapshot(SensorsSnapshotAvro snapshot, SensorEventAvro event) {
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), newState);

        return snapshot;
    }
}
