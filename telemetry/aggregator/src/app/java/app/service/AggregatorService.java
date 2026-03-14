package app.java.app.service;

//import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

//import java.time.Duration;

//import java.util.ArrayList;
//import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.function.Function;

@Slf4j
@Service
public class AggregatorService {
//    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;
//
//    @Value("${kafka.values.timeout}")
//    private int consumeTimeout;
//
//    @Value("${kafka.topics.snapshot}")
//    private String snapshotTopic;

    private final ConcurrentMap<String, SensorsSnapshotAvro> snapshots;

    public AggregatorService() {
        this.snapshots = new ConcurrentHashMap<>();
    }

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
//        if (!snapshots.containsKey(event.getHubId())) {
//            snapshots.putIfAbsent(event.getHubId(), new CopyOnWriteArrayList<>());
//        }
//
//        SensorsSnapshotAvro snapshot = snapshots.get(event.getHubId()).stream()
//                .filter(e -> e.getSensorsState().containsKey(event.getId()))
//                .findFirst().orElse(
//                        SensorsSnapshotAvro.newBuilder()
//                            .setHubId(event.getHubId())
//                            .setTimestamp(event.getTimestamp())
//                            .setSensorsState(Map.of(event.getId(), SensorStateAvro.newBuilder()
//                                    .setTimestamp(event.getTimestamp())
//                                    .setData(event.getPayload()).build()))
//                            .build());
//
//        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
//
//        if (oldState.getTimestamp().isBefore(event.getTimestamp())) {
//            return Optional.empty();
//        }
//
//        SensorStateAvro newState = SensorStateAvro.newBuilder()
//                .setTimestamp(event.getTimestamp())
//                .setData(event.getPayload())
//                .build();
//
//        snapshot.getSensorsState().put(event.getId(), newState);

        SensorsSnapshotAvro snapshot;

        if (snapshots.containsKey(event.getHubId())) {
            snapshot = snapshots.get(event.getHubId());
        } else {
            snapshot = SensorsSnapshotAvro.newBuilder()
                            .setHubId(event.getHubId())
                            .setTimestamp(event.getTimestamp())
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

        return Optional.of(snapshot);

//        SensorsSnapshotAvro snapshot = null;
//
//        try {
//            snapshotConsumer.subscribe(List.of(snapshotTopic));
//
//            ConsumerRecords<String, SensorsSnapshotAvro> records =
//                    snapshotConsumer.poll(Duration.ofMillis(consumeTimeout));
//
//            for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
//                if (record.value().getHubId().equals(event.getHubId())) {
//                    snapshot = record.value();
//                }
//            }
//
//            if (snapshot == null) {
//                snapshot = SensorsSnapshotAvro.newBuilder()
//                        .setHubId(event.getHubId())
//                        .setTimestamp(event.getTimestamp())
//                        .setSensorsState(Map.of(event.getId(), SensorStateAvro.newBuilder()
//                                .setTimestamp(event.getTimestamp())
//                                .setData(event.getPayload()).build()))
//                        .build();
//            }
//
//            if (snapshot.getSensorsState().containsKey(event.getId())) {
//                SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
//
//                if (oldState.getTimestamp().isBefore(event.getTimestamp())
//                        || oldState.getData().equals(event.getPayload())) {
//                    return Optional.empty();
//                }
//            }
//
//            updateSensorsSnapshot(snapshot, event);
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        } finally {
//            if (snapshotConsumer != null) {
//                try {
//                    snapshotConsumer.close();
//                } catch (Exception e) {
//                    log.error(e.getMessage());
//                }
//            }
//        }
//
//        return Optional.of(snapshot);
    }

//     private void updateSensorsSnapshot(SensorsSnapshotAvro snapshot, SensorEventAvro event) {
//        SensorStateAvro newState = SensorStateAvro.newBuilder()
//                .setTimestamp(event.getTimestamp())
//                .setData(event.getPayload())
//                .build();
//
//        snapshot.getSensorsState().put(event.getId(), newState);
//    }

    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        snapshots.putIfAbsent(event.getHubId(), event);
    }
}