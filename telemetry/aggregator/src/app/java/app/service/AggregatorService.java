package app.java.app.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class AggregatorService {
    private final ConcurrentMap<String, SensorsSnapshotAvro> snapshots;

    public AggregatorService() {
        this.snapshots = new ConcurrentHashMap<>();
    }

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info("Получен список снимков {}", snapshots);

        SensorsSnapshotAvro snapshot;

        if (snapshots.containsKey(event.getHubId())) {
            log.info("В снимке найдено событие {}", event);

            snapshot = snapshots.get(event.getHubId());

            if (snapshot.getSensorsState().containsKey(event.getId())) {
                log.info("В снимке {} найдено событие {}", snapshot, event);

                SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

                if (oldState.getTimestamp().isAfter(event.getTimestamp())
                        || oldState.getData().equals(event.getPayload())) {
                    log.info("Событие {} не нужно изминениять", event);

                    return Optional.empty();
                }
            }

            SensorStateAvro newState = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();

            snapshot.getSensorsState().put(event.getId(), newState);

            log.info("Сенсор {} в снимке {} был изменен и готов к отправке", newState, snapshot);
        } else {
            log.info("В снимке не найдено событие {}", event);

            snapshot = SensorsSnapshotAvro.newBuilder()
                            .setHubId(event.getHubId())
                            .setTimestamp(event.getTimestamp())
                            .setSensorsState(Map.of(event.getId(), SensorStateAvro.newBuilder()
                                    .setTimestamp(event.getTimestamp())
                                    .setData(event.getPayload()).build()))
                            .build();

            log.info("Создан новый снимок {}", snapshot);
        }

        return Optional.of(snapshot);
    }

    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        snapshots.putIfAbsent(event.getHubId(), event);
    }
}