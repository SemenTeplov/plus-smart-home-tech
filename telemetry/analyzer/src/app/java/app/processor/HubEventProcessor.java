package app.java.app.processor;

import app.java.app.constant.Message;
import app.java.app.model.Action;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.model.ScenarioAction;
import app.java.app.model.ScenarioActionId;
import app.java.app.model.ScenarioCondition;
import app.java.app.model.ScenarioConditionId;
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

import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
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
        log.info(Message.GET_HUB_EVENT, event.getHubId());

        if (event.getPayload().getClass().equals(ScenarioAddedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            ScenarioAddedEventAvro eventAvro = (ScenarioAddedEventAvro) event.getPayload();

            Scenario scenario = getScenario(event.getHubId(), eventAvro.getName());

            for (var conditionItem : eventAvro.getConditions()) {
                for (var actionItem : eventAvro.getActions()) {
                    log.info(Message.WORK_SCENARIO_CONDITION,
                            conditionItem.getType().name(),
                            conditionItem.getOperation().name(),
                            conditionItem.getValue());

                    log.info(Message.WORK_SCENARIO_ACTION,
                            actionItem.getType().name(),
                            actionItem.getValue());

                    Condition condition = getCondition(
                            conditionItem.getType().name(),
                            conditionItem.getOperation().name(),
                            conditionItem.getValue(),
                            scenario.getConditions().stream()
                                    .map(ScenarioCondition::getCondition)
                                    .collect(Collectors.toSet()));

                    Action action = getAction(
                            actionItem.getType().name(),
                            actionItem.getValue(),
                            scenario.getActions().stream().map(ScenarioAction::getAction)
                                    .collect(Collectors.toSet()));

                    Sensor sensor = getSensor(event, conditionItem.getSensorId());

                    ScenarioCondition scenarioCondition = scenarioConditionRepository.save(
                            ScenarioCondition.builder()
                                    .scenario(scenario)
                                    .condition(condition)
                                    .sensor(sensor)
                                    .id(new ScenarioConditionId(scenario.getId(), sensor.getId(), condition.getId()))
                                    .build());

                    log.info(Message.CREATED_SCENARIO_CONDITION, scenarioCondition);

                    scenario.addCondition(scenarioCondition);
                    condition.addCondition(scenarioCondition);
                    sensor.addCondition(scenarioCondition);

                    ScenarioAction scenarioAction = scenarioActionRepository.save(
                            ScenarioAction.builder()
                                    .scenario(scenario)
                                    .action(action)
                                    .sensor(sensor)
                                    .id(new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId()))
                                    .build());

                    log.info(Message.CREATED_SCENARIO_ACTION, scenarioAction);

                    scenario.addAction(scenarioAction);
                    action.addAction(scenarioAction);
                    sensor.addAction(scenarioAction);
                }
            }

        for (var item : eventAvro.getActions()) {
//            log.info("Обработка ScenarioAction: Type - {}, Value - {}",
//                    item.getType().name(),
//                    item.getValue());

//                Action action = getAction(
//                        item.getType().name(),
//                        item.getValue(),
//                        scenario.getActions().stream().map(ScenarioAction::getAction)
//                                .collect(Collectors.toSet()));

//                ScenarioAction scenarioAction = scenarioActionRepository.save(
//                        ScenarioAction.builder()
//                                .scenario(scenario)
//                                .action(action)
//                                .sensor(sensor)
//                                .id(new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId()))
//                                .build());

//                log.info("Был создан ScenarioAction: {}", scenarioAction);
//
//                scenario.addAction(scenarioAction);
//                action.addAction(scenarioAction);
//                sensor.addAction(scenarioAction);
            }

        } else if (event.getPayload().getClass().equals(DeviceAddedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            DeviceAddedEventAvro eventAvro = (DeviceAddedEventAvro) event.getPayload();

            getSensor(event, eventAvro.getId());
        } else if (event.getPayload().getClass().equals(ScenarioRemovedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            ScenarioRemovedEventAvro eventAvro = (ScenarioRemovedEventAvro) event.getPayload();

            scenarioRepository
                    .findByHubIdAndName(event.getHubId(), eventAvro.getName()).ifPresent(scenarioRepository::delete);
        } else if (event.getPayload().getClass().equals(DeviceRemovedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            DeviceRemovedEventAvro eventAvro = (DeviceRemovedEventAvro) event.getPayload();

            sensorRepository
                    .findByIdAndHubId(eventAvro.getId(), event.getHubId()).ifPresent(sensorRepository::delete);
        }
    }

    private Sensor getSensor(HubEventAvro event, String id) {
        if (event == null) {
            throw new IllegalArgumentException("В методе getSensor не может event быть null");
        }

        if (id == null) {
            throw new IllegalArgumentException("В методе getSensor не может id быть null");
        }

        Sensor sensor = sensorRepository
                .findByIdAndHubId(id, event.getHubId())
                .orElse(sensorRepository.saveAndFlush(Sensor.builder()
                                .hubId(event.getHubId())
                                .id(id)
                                .conditions(new HashSet<>())
                                .actions(new HashSet<>())
                                .build()));

        log.info("Получен Sensor: {}", sensor);

        return sensor;
    }

    private Scenario getScenario(String hubId, String name) {
        if (hubId == null) {
            throw new IllegalArgumentException("В методе getScenario не может hubId быть null");
        }

        if (name == null) {
            throw new IllegalArgumentException("В методе getScenario не может name быть null");
        }

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(hubId, name)
                .orElse(scenarioRepository.saveAndFlush(Scenario.builder()
                        .hubId(hubId)
                        .name(name)
                        .conditions(new HashSet<>())
                        .actions(new HashSet<>())
                        .build()));

        log.info("Получен Scenario: {}", scenario);

        return scenario;
    }

    private Condition getCondition(String type, String operation, Integer value, Set<Condition> set) {
        if (type == null) {
            throw new IllegalArgumentException("В методе getCondition не может type быть null");
        }

        if (operation == null) {
            throw new IllegalArgumentException("В методе getCondition не может operation быть null");
        }

        if (value == null) {
            throw new IllegalArgumentException("В методе getCondition не может value быть null");
        }

        Condition condition =  set.stream()
                .filter(i ->
                        i.getType().equals(type) &&
                                operation.equals(i.getOperation()) &&
                                value.equals(i.getValue()))
                .findFirst()
                .orElse(conditionRepository.saveAndFlush(
                        Condition.builder()
                                .type(type)
                                .operation(operation)
                                .value(value)
                                .conditions(new HashSet<>())
                                .build()));

        log.info("Получен Condition {}", condition);

        return condition;
    }

    private Action getAction(String type, Integer value, Set<Action> set) {
        if (type == null) {
            throw new IllegalArgumentException("В методе getAction не может type быть null");
        }

        if (ActionTypeAvro.ACTIVATE.name().equals(type)) {
            value = 1;
        } else if (ActionTypeAvro.DEACTIVATE.name().equals(type)) {
            value = 0;
        }

        Action action = set.stream()
                .filter(i -> type.equals(i.getType()))
                .findFirst()
                .orElse(actionRepository.saveAndFlush(
                        Action.builder()
                                .type(type)
                                .value(value)
                                .actions(new HashSet<>())
                                .build()));

        log.info("Получен Action: {}", action);

        return action;
    }
}
