package app.java.app.processor;

import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.model.Scenario;
import app.java.app.action.ActionInterface;
import app.java.app.model.Sensor;
import app.java.app.repository.ScenarioRepository;

import app.java.app.repository.SensorRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

@Slf4j
@Service
public class SnapshotProcessor {
    private final SensorRepository sensorRepository;

    private final List<ActionInterface> actions;

    @Autowired
    public SnapshotProcessor(
            List<ActionInterface> actions,
            SensorRepository sensorRepository) {
        this.actions = actions;
        this.sensorRepository = sensorRepository;
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        log.info("Поступил SensorsSnapshotAvro с HubId: {}", event.getHubId());

        event.getSensorsState().entrySet()
            .forEach(e -> {
                sensorRepository.findByIdAndHubId(e.getKey(), event.getHubId()).ifPresent(s -> {
                    List<ConditionDto> conditionsDto = s.getConditions().stream()
                        .map(c -> ConditionDto.builder()
                                .scenario(c.getScenario())
                                .condition(c.getCondition())
                                .sensor(c.getSensor()).build())
                        .toList();

                    log.info("Список ConditionDto: {}", conditionsDto);

                    List<ActionDto> actionsDto = s.getActions().stream()
                        .filter(a -> conditionsDto.stream()
                                .map(c -> c.getScenario().getName())
                                .toList()
                                .contains(a.getScenario().getName()))
                        .map(a -> ActionDto.builder()
                                .scenario(a.getScenario())
                                .sensor(a.getSensor())
                                .action(a.getAction()).build())
                        .toList();

                    log.info("Список ActionDto: {}", conditionsDto);

                    actions.stream()
                        .peek(a -> log.info("action type: {}, condition type: {}", a.getType(), conditionsDto.getFirst().getCondition().getType()))
                        .filter(a -> a.getType()
                                .equals(conditionsDto.getFirst().getCondition().getType()))
                        .forEach(a -> {
                            a.sendAction(e.getValue(), conditionsDto, actionsDto);
                        });
                });
            });
    }
}
