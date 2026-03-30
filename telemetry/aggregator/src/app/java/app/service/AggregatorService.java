package app.java.app.service;

import app.java.app.constant.Message;
import app.java.app.constant.Values;

import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AggregatorService {
    private final Map<String, SensorsSnapshotAvro> snapshots;

    public AggregatorService() {
        this.snapshots = new ConcurrentHashMap<>();
    }


    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        log.info(Message.GET_LIST_OF_SNAPSHOTS, snapshots);

        SensorsSnapshotAvro snapshot;

        if (snapshots.containsKey(event.getHubId())) {
            log.info(Message.FINED_EVENT, event);

            snapshot = snapshots.get(event.getHubId());

            if (snapshot.getSensorsState().containsKey(event.getId())) {
                log.info(Message.FINED_EVENT_DETAIL, snapshot, event);

                SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());

                if (oldState.getTimestamp().isBefore(event.getTimestamp())
                        || oldState.getData().equals(event.getPayload())) {
                    log.info(Message.EVENT_DO_NOT_CHANGE, event);

                    return Optional.empty();
                }
            }

            SensorStateAvro newState = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();

            snapshot.getSensorsState().put(event.getId(), newState);
            snapshot.setTimestamp(event.getTimestamp());

            log.info(Message.EVENT_CHANGED, newState, snapshot);
        } else {
            log.info(Message.DO_NOT_FOUND_EVENT, event);

            Map<String, SensorStateAvro> states = new HashMap<>();
            states.put(event.getId(), SensorStateAvro.newBuilder()
                        .setTimestamp(event.getTimestamp())
                        .setData(event.getPayload()).build());

            snapshot = SensorsSnapshotAvro.newBuilder()
                            .setHubId(event.getHubId())
                            .setTimestamp(event.getTimestamp())
                            .setSensorsState(states)
                            .build();

            log.info(Message.CREATED_SNAPSHOT, snapshot);
        }

        return Optional.of(snapshot);
    }

    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = Values.SNAPSHOT_CONSUMER)
    public void handler(SensorsSnapshotAvro event, Acknowledgment acknowledgment) {
        if (!snapshots.containsKey(event.getHubId())) {
            snapshots.putIfAbsent(event.getHubId(), event);
        } else {
            snapshots.replace(event.getHubId(), event);
        }

        acknowledgment.acknowledge();
    }
}