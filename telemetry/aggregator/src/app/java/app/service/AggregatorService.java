package app.java.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;

import java.util.List;
import java.util.Map;
import java.util.Optional;

//@Slf4j
//@Service
//public class AggregatorService {
//    @Qualifier("snapshotConsumer")
//    private final Consumer<String, SensorsSnapshotAvro> snapshotConsumer;
//
//    @Value("${kafka.values.timeout}")
//    private int consumeTimeout;
//
//    @Value("${kafka.topics.snapshot}")
//    private String snapshotTopic;
//
//    @Autowired
//    public AggregatorService(Consumer<String, SensorsSnapshotAvro> snapshotConsumer) {
//        this.snapshotConsumer = snapshotConsumer;
//    }
//
//    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
//        snapshotConsumer.subscribe(List.of(snapshotTopic));
//
//        try {
//            SensorsSnapshotAvro snapshot = getSensorsSnapshots(event);
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
//            return Optional.of(createSensorsSnapshot(snapshot, event));
//
//        } catch (Exception e) {
//            log.error(e.getMessage());
//
//            return Optional.empty();
//        } finally {
//            snapshotConsumer.commitSync();
//            snapshotConsumer.close();
//        }
//    }
//
//    private SensorsSnapshotAvro getSensorsSnapshots(SensorEventAvro event) {
//        ConsumerRecords<String, SensorsSnapshotAvro> records =
//                snapshotConsumer.poll(Duration.ofMillis(consumeTimeout));
//
//        for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
//            if (record.value().getHubId().equals(event.getHubId())) {
//                return record.value();
//            }
//        }
//
//        return SensorsSnapshotAvro.newBuilder()
//                .setHubId(event.getHubId())
//                .setTimestamp(event.getTimestamp())
//                .setSensorsState(Map.of(event.getId(), SensorStateAvro.newBuilder()
//                        .setTimestamp(event.getTimestamp())
//                        .setData(event.getPayload()).build()))
//                .build();
//    }
//
////    private boolean shouldUpdate(SensorsSnapshotAvro snapshot, SensorEventAvro event) {
////        if (!snapshot.getSensorsState().containsKey(event.getId())) {
////            return true;
////        }
////
////        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
////
////        return !oldState.getTimestamp().isBefore(event.getTimestamp())
////                || !oldState.getData().equals(event.getPayload());
////    }
//
//    private SensorsSnapshotAvro createSensorsSnapshot(SensorsSnapshotAvro snapshot, SensorEventAvro event) {
//        SensorStateAvro newState = SensorStateAvro.newBuilder()
//                .setTimestamp(event.getTimestamp())
//                .setData(event.getPayload())
//                .build();
//
//        snapshot.getSensorsState().put(event.getId(), newState);
//
//        return snapshot;
//    }
//}

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