package app.java.app.processor;

import app.java.app.model.Action;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.model.Sensor;
import app.java.app.repository.ActionRepository;
import app.java.app.repository.ConditionRepository;
import app.java.app.repository.ScenarioRepository;
import app.java.app.repository.SensorRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class HubEventProcessor {
    private final ScenarioRepository scenarioRepository;

    private final ConditionRepository conditionRepository;

    private final ActionRepository actionRepository;

    private final SensorRepository sensorRepository;

    @Autowired
    public HubEventProcessor(ScenarioRepository scenarioRepository,
                             ConditionRepository conditionRepository,
                             ActionRepository actionRepository,
                             SensorRepository sensorRepository) {
        this.scenarioRepository = scenarioRepository;
        this.conditionRepository = conditionRepository;
        this.actionRepository = actionRepository;
        this.sensorRepository = sensorRepository;
    }

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
                        scenario.getConditions());

                Sensor sensor = getSensor(event, item.getSensorId());
                sensor.addCondition(condition);

                scenario.addSensorForCondition(sensor);
                scenario.addCondition(condition);
            }

            for (var item : eventAvro.getActions()) {
                Action action = getAction(
                        item.getType().name(),
                        item.getValue(),
                        scenario.getActions());

                Sensor sensor = getSensor(event, item.getSensorId());
                sensor.addAction(action);

                scenario.addSensorForAction(sensor);
                scenario.addAction(action);
            }

            scenarioRepository.save(scenario);

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
                        .sensorsForActions(new HashSet<>())
                        .sensorsForCondition(new HashSet<>())
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
                            .build()));
    }
}
