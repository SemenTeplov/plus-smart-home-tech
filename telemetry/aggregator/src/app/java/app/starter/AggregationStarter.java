package app.java.app.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import app.java.app.service.AggregatorService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final AggregatorService service;

    private final CopyOnWriteArrayList<SensorEventAvro> sensors = new CopyOnWriteArrayList<>();

    private final KafkaTemplate<String, SensorsSnapshotAvro> template;

    @Value("${kafka.topics.snapshot}")
    private String snapshotTopic;

    @Scheduled(fixedDelay = 5000)
    public void sendSnapshots() {
        for (SensorEventAvro sensor : sensors) {
            Optional<SensorsSnapshotAvro> snapshot = service.updateState(sensor);

            snapshot.ifPresent(sensorsSnapshotAvro -> template.send(snapshotTopic, sensorsSnapshotAvro));
        }
    }

    @KafkaListener(topics = "${kafka.topics.sensor}", containerFactory = "eventConsumer")
    public void handler(SensorEventAvro event) {
        sensors.addIfAbsent(event);
    }
}
