package app.java.app.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import app.java.app.service.AggregatorService;
import app.java.app.constant.Values;
import app.java.app.constant.Message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final AggregatorService service;

    private final CopyOnWriteArrayList<SensorEventAvro> sensors = new CopyOnWriteArrayList<>();

    private final KafkaTemplate<String, SensorsSnapshotAvro> template;

    @Value("${aggregator.kafka.topics.snapshot}")
    private String snapshotTopic;

    @Scheduled(fixedDelay = Values.FIXED_DELAY)
    public void sendSnapshots() {
        log.info(Message.GET_LIST_OF_SENSORS, sensors);

        List<SensorEventAvro> temp = new ArrayList<>(sensors);

        for (SensorEventAvro sensor : temp) {
            Optional<SensorsSnapshotAvro> snapshot = service.updateState(sensor);
            sensors.remove(sensor);

            snapshot.ifPresent(sensorsSnapshotAvro -> template.send(snapshotTopic, sensorsSnapshotAvro));
        }
    }

    @KafkaListener(topics = "${aggregator.kafka.topics.sensor}", containerFactory = Values.EVENT_CONSUMER)
    public void handler(SensorEventAvro event, Acknowledgment acknowledgment) {
        sensors.addIfAbsent(event);
        acknowledgment.acknowledge();
    }
}
