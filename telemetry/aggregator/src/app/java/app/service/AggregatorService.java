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
import java.time.Instant;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorService {
    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;

    private static final int CONSUME_ATTEMPT_TIMEOUT = 100;

    private SensorsSnapshotAvro snapshot = null;

    @Value("${kafka.topics.snapshot}")
    private String snapshotTopic;

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        try {
            snapshotConsumer.subscribe(List.of(snapshotTopic));

            ConsumerRecords<String, SensorsSnapshotAvro> records =
                    snapshotConsumer.poll(Duration.ofMillis(CONSUME_ATTEMPT_TIMEOUT));

            for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                if (record.value().getHubId().equals(event.getHubId())) {
                    snapshot = record.value();
                }
            }

            if (snapshot == null) {
                snapshot = SensorsSnapshotAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant.now())
                        .setSensorsState(Map.of(event.getId(), SensorStateAvro.newBuilder()
                                .setTimestamp(event.getTimestamp())
                                .setData(event.getPayload()).build()))
                        .build();
            }

            if (snapshot.getSensorsState().containsKey(event.getId())) {
                SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

                if (oldState.getTimestamp().isBefore(event.getTimestamp())
                        || oldState.getData().equals(event.getPayload())) {
                    return Optional.empty();
                }
            }

            SensorStateAvro newState = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();

            snapshot.getSensorsState().put(event.getId(), newState);
            snapshot.setTimestamp(event.getTimestamp());

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (snapshotConsumer != null) {
                try {
                    snapshotConsumer.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }

        return Optional.of(snapshot);
    }
}
