package app.java.app.processor;

import app.java.app.constant.Exceptions;
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
import org.springframework.kafka.support.Acknowledgment;
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
    @KafkaListener(topics = "${analyzer.kafka.topics.hub}", containerFactory = "hubConsumer")
    public void handler(HubEventAvro event, Acknowledgment acknowledgment) {
        log.info(Message.GET_HUB_EVENT, event.getHubId());

        if (event.getPayload().getClass().equals(ScenarioAddedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            ScenarioAddedEventAvro eventAvro = (ScenarioAddedEventAvro) event.getPayload();

            Scenario scenario = getScenario(event.getHubId(), eventAvro.getName());

            eventAvro.getActions().forEach(actionItem -> {
                log.info(Message.WORK_SCENARIO_ACTION,
                        actionItem.getType().name(),
                        actionItem.getValue());

                Action action = getAction(
                        actionItem.getType().name(),
                        actionItem.getValue(),
                        scenario.getActions().stream().map(ScenarioAction::getAction)
                                .collect(Collectors.toSet()));

                Sensor sensorAction = getSensor(event, actionItem.getSensorId());

                ScenarioActionId saId = new ScenarioActionId(
                        scenario.getId(), sensorAction.getId(), action.getId());

                if (scenarioActionRepository.findById(saId).isEmpty()) {
                    ScenarioAction scenarioAction =
                            ScenarioAction.builder()
                                    .scenario(scenario)
                                    .action(action)
                                    .sensor(sensorAction)
                                    .id(saId)
                                    .build();

                    log.info(Message.CREATED_SCENARIO_ACTION, scenarioAction);

                    scenarioActionRepository.save(scenarioAction);

                    scenario.addAction(scenarioAction);
                    sensorAction.addAction(scenarioAction);
                }
            });

            eventAvro.getConditions().forEach(conditionItem -> {
                log.info(Message.WORK_SCENARIO_CONDITION,
                        conditionItem.getType().name(),
                        conditionItem.getOperation().name(),
                        conditionItem.getValue());

                Condition condition = getCondition(
                        conditionItem.getType().name(),
                        conditionItem.getOperation().name(),
                        conditionItem.getValue(),
                        scenario.getConditions().stream()
                                .map(ScenarioCondition::getCondition)
                                .collect(Collectors.toSet()));

                Sensor sensorCondition = getSensor(event, conditionItem.getSensorId());

                ScenarioConditionId scId = new ScenarioConditionId(
                        scenario.getId(), sensorCondition.getId(), condition.getId());

                if (scenarioConditionRepository.findById(scId).isEmpty()) {
                    ScenarioCondition scenarioCondition =
                            ScenarioCondition.builder()
                                    .scenario(scenario)
                                    .condition(condition)
                                    .sensor(sensorCondition)
                                    .id(scId)
                                    .build();

                    log.info(Message.CREATED_SCENARIO_CONDITION, scenarioCondition);

                    scenarioConditionRepository.save(scenarioCondition);

                    scenario.addCondition(scenarioCondition);
                    sensorCondition.addCondition(scenarioCondition);
                }
            });

            acknowledgment.acknowledge();
        } else if (event.getPayload().getClass().equals(DeviceAddedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            DeviceAddedEventAvro eventAvro = (DeviceAddedEventAvro) event.getPayload();

            getSensor(event, eventAvro.getId());

            acknowledgment.acknowledge();
        } else if (event.getPayload().getClass().equals(ScenarioRemovedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            ScenarioRemovedEventAvro eventAvro = (ScenarioRemovedEventAvro) event.getPayload();

            scenarioRepository
                    .findByHubIdAndName(event.getHubId(), eventAvro.getName()).ifPresent(scenarioRepository::delete);

            acknowledgment.acknowledge();
        } else if (event.getPayload().getClass().equals(DeviceRemovedEventAvro.class)) {
            log.info(Message.HUB_EVENT_NAME, event.getPayload().getClass().getSimpleName());

            DeviceRemovedEventAvro eventAvro = (DeviceRemovedEventAvro) event.getPayload();

            sensorRepository
                    .findByIdAndHubId(eventAvro.getId(), event.getHubId()).ifPresent(sensorRepository::delete);

            acknowledgment.acknowledge();
        }
    }

    private Sensor getSensor(HubEventAvro event, String id) {
        if (event == null) {
            throw new IllegalArgumentException(Exceptions.SENSOR_IS_NOT_NULL);
        }

        if (id == null) {
            throw new IllegalArgumentException(Exceptions.ID_IS_NOT_NULL);
        }

        Sensor sensor = sensorRepository
                .findByIdAndHubId(id, event.getHubId())
                .orElse(sensorRepository.saveAndFlush(Sensor.builder()
                                .hubId(event.getHubId())
                                .id(id)
                                .conditions(new HashSet<>())
                                .actions(new HashSet<>())
                                .build()));

        log.info(Message.GET_SENSOR, sensor);

        return sensor;
    }

    private Scenario getScenario(String hubId, String name) {
        if (hubId == null) {
            throw new IllegalArgumentException(Exceptions.HUB_ID_IS_NOT_NULL);
        }

        if (name == null) {
            throw new IllegalArgumentException(Exceptions.SCENARIO_IS_NOT_NULL);
        }

        Scenario scenario = scenarioRepository
                .findByHubIdAndName(hubId, name)
                .orElse(scenarioRepository.saveAndFlush(Scenario.builder()
                        .hubId(hubId)
                        .name(name)
                        .conditions(new HashSet<>())
                        .actions(new HashSet<>())
                        .build()));

        log.info(Message.GET_SCENARIO, scenario);

        return scenario;
    }

    private Condition getCondition(String type, String operation, Integer value, Set<Condition> set) {
        if (type == null) {
            throw new IllegalArgumentException(Exceptions.TYPE_IS_NOT_NULL);
        }

        if (operation == null) {
            throw new IllegalArgumentException(Exceptions.OPERATION_IS_NOT_NULL);
        }

        if (value == null) {
            throw new IllegalArgumentException(Exceptions.VALUE_IS_NOT_NULL);
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
                                .build()));

        log.info(Message.GET_CONDITION, condition);

        return condition;
    }

    private Action getAction(String type, Integer value, Set<Action> set) {
        if (type == null) {
            throw new IllegalArgumentException(Exceptions.ACTION_TYPE_IS_NOT_NULL);
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
                                .build()));

        log.info(Message.GET_ACTION, action);

        return action;
    }
}
