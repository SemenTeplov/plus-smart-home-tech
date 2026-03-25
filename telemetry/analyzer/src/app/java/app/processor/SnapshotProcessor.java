package app.java.app.processor;

import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.action.ActionInterface;
import app.java.app.constant.Message;
//import app.java.app.model.ScenarioAction;
//import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ScenarioActionRepository;
import app.java.app.repository.ScenarioConditionRepository;
//import app.java.app.repository.ScenarioRepository;
import app.java.app.repository.SensorRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SnapshotProcessor {
    private final SensorRepository sensorRepository;

    private final List<ActionInterface> actions;

    private final ScenarioConditionRepository scenarioConditionRepository;

    private final ScenarioActionRepository scenarioActionRepository;

    @Autowired
    public SnapshotProcessor(
            List<ActionInterface> actions,
            SensorRepository sensorRepository,
            ScenarioConditionRepository scenarioConditionRepository,
            ScenarioActionRepository scenarioActionRepository) {
        this.actions = actions;
        this.sensorRepository = sensorRepository;
        this.scenarioConditionRepository = scenarioConditionRepository;
        this.scenarioActionRepository = scenarioActionRepository;
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        log.info(Message.GET_SENSORS_SNAPSHOT, event.getHubId());

        List<ActionDto> actionsDto = new ArrayList<>();

        List<ConditionDto> conditionsDto = new ArrayList<>();

        event.getSensorsState().entrySet()
            .forEach(e -> {
                ConditionDto condition = scenarioConditionRepository.findAll().stream()
                        .filter(sc -> sc.getSensor().getId().equals(e))
                        .map(sc ->
                                ConditionDto.builder()
                                    .scenario(sc.getScenario())
                                    .condition(sc.getCondition())
                                    .sensor(sc.getSensor()).build())
                        .findFirst()
                        .get();

                ActionDto action = scenarioActionRepository.findAll().stream()
                        .filter(sa -> sa.getScenario().equals(condition.getScenario()))
                        .map(sa ->
                                ActionDto.builder()
                                    .action(sa.getAction())
                                    .scenario(sa.getScenario())
                                    .sensor(sa.getSensor())
                                    .build())
                        .findFirst().get();

                actions.stream()
                        .filter(a -> a.getActionClass().getName()
                                .equals(e.getValue().getData().getClass().getName()))
                        .forEach(a -> {
                            a.sendAction(e.getValue().getData(), condition, action);
                        });

//                sensorRepository.findByIdAndHubId(e.getKey(), event.getHubId()).ifPresent(s -> {
//                    log.info("Найден Sensor: {}", s);
//                    conditionsDto.addAll(s.getConditions().stream()
//                        .map(c -> ConditionDto.builder()
//                                .scenario(c.getScenario())
//                                .condition(c.getCondition())
//                                .sensor(c.getSensor()).build())
//                        .toList());
//
//                    actionsDto.addAll(conditionsDto.stream()
//                            .flatMap(c -> c.getScenario().getActions().stream())
//                            .map(c -> ActionDto.builder()
//                                    .action(c.getAction())
//                                    .scenario(c.getScenario())
//                                    .sensor(c.getSensor())
//                                    .build())
//                            .toList());
//
//                    log.info("Conditions: {}", conditionsDto);
//                    log.info("Actions: {}", actionsDto);
//
//                    actions.stream()
//                        .filter(a -> a.getActionClass().getName()
//                                .equals(e.getValue().getData().getClass().getName()))
//                        .forEach(a -> {
//                            a.sendAction(e.getValue().getData(), conditionsDto, actionsDto);
//                        });
//                });
            });
    }
}
