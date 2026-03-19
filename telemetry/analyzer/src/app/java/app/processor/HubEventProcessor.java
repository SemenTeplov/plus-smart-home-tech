package app.java.app.processor;

import app.java.app.model.Action;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.model.ScenarioAction;
import app.java.app.model.ScenarioCondition;
import app.java.app.model.Sensor;
import app.java.app.repository.ActionRepository;
import app.java.app.repository.ConditionRepository;
import app.java.app.repository.ScenarioActionRepository;
import app.java.app.repository.ScenarioConditionRepository;
import app.java.app.repository.ScenarioRepository;
import app.java.app.repository.SensorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventProcessor {
    private final ScenarioRepository scenarioRepository;

    private final ConditionRepository conditionRepository;

    private final ActionRepository actionRepository;

    private final SensorRepository sensorRepository;

    private final ScenarioConditionRepository scenarioConditionRepository;

    private final ScenarioActionRepository scenarioActionRepository;

    @Transactional
    @KafkaListener(topics = "${kafka.topics.hub}", containerFactory = "hubConsumer")
    public void handler(HubEventAvro event) {
        if (event.getPayload().getClass().equals(ScenarioAddedEventAvro.class)) {
            ScenarioAddedEventAvro eventAvro = (ScenarioAddedEventAvro) event.getPayload();

            Scenario scenario = getScenario(event.getHubId(), eventAvro.getName());

            for (var item : eventAvro.getConditions()) {
                Condition condition = getCondition(
                        item.getType().name(),
                        item.getOperation().name(),
                        item.getValue(),
                        scenario.getConditions().stream()
                                .map(ScenarioCondition::getCondition)
                                .collect(Collectors.toSet()));

                Sensor sensor = getSensor(event, item.getSensorId());

                ScenarioCondition scenarioCondition = scenarioConditionRepository.save(
                        ScenarioCondition.builder()
                            .scenario(scenario)
                            .condition(condition)
                            .sensor(sensor)
                            .build());

                scenario.addCondition(scenarioCondition);
                condition.addCondition(scenarioCondition);
                sensor.addCondition(scenarioCondition);
            }

            for (var item : eventAvro.getActions()) {
                Action action = getAction(
                        item.getType().name(),
                        item.getValue(),
                        scenario.getActions().stream().map(ScenarioAction::getAction)
                                .collect(Collectors.toSet()));

                Sensor sensor = getSensor(event, item.getSensorId());

                ScenarioAction scenarioAction = scenarioActionRepository.save(
                        ScenarioAction.builder()
                            .scenario(scenario)
                            .action(action)
                            .sensor(sensor)
                            .build());

                scenario.addAction(scenarioAction);
                action.addAction(scenarioAction);
                sensor.addAction(scenarioAction);
            }

        } else if (event.getPayload().getClass().equals(ScenarioRemovedEventAvro.class)) {
            ScenarioRemovedEventAvro eventAvro = (ScenarioRemovedEventAvro) event.getPayload();

            scenarioRepository
                    .findByHubIdAndName(event.getHubId(), eventAvro.getName()).ifPresent(scenarioRepository::delete);
        }
    }

    private Sensor getSensor(HubEventAvro event, String id) {
        return sensorRepository.findByIdAndHubId(id, event.getHubId()).orElse(sensorRepository.saveAndFlush(
                Sensor.builder()
                    .hubId(event.getHubId())
                    .id(id)
                    .conditions(new HashSet<>())
                    .actions(new HashSet<>())
                    .build()));
    }

    private Scenario getScenario(String hubId, String name) {
        return scenarioRepository.findByHubIdAndName(hubId, name).orElse(
                Scenario.builder()
                        .hubId(hubId)
                        .name(name)
                        .conditions(new HashSet<>())
                        .actions(new HashSet<>())
                        .build());
    }

    private Condition getCondition(String type, String operation, Integer value, Set<Condition> set) {
        return set.stream()
                .filter(i ->
                        i.getType().equals(type) &&
                        i.getOperation().equals(operation) &&
                        i.getValue().equals(value))
                .findFirst()
                .orElse(conditionRepository.saveAndFlush(
                        Condition.builder()
                                .type(type)
                                .operation(operation)
                                .value(value)
                                .conditions(new HashSet<>())
                                .build()));
    }

    private Action getAction(String type, Integer value, Set<Action> set) {
        return set.stream()
                .filter(i -> i.getType().equals(type) && i.getValue().equals(value))
                .findFirst()
                .orElse(actionRepository.saveAndFlush(
                        Action.builder()
                            .type(type)
                            .value(value)
                            .actions(new HashSet<>())
                            .build()));
    }
}
