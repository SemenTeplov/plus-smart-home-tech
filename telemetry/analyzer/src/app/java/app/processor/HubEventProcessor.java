package app.java.app.processor;

import app.java.app.model.Action;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.model.Sensor;
import app.java.app.repository.ActionRepository;
import app.java.app.repository.ConditionRepository;
import app.java.app.repository.ScenarioRepository;

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

    @Autowired
    public HubEventProcessor(ScenarioRepository scenarioRepository,
                             ConditionRepository conditionRepository,
                             ActionRepository actionRepository) {
        this.scenarioRepository = scenarioRepository;
        this.conditionRepository = conditionRepository;
        this.actionRepository = actionRepository;
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topics.hub}", containerFactory = "hubConsumer")
    public void handler(HubEventAvro event) {
        if (event.getPayload().getClass().equals(ScenarioAddedEventAvro.class)) {
            ScenarioAddedEventAvro eventAvro = (ScenarioAddedEventAvro) event.getPayload();

            Scenario scenario = Scenario.builder()
                    .hubId(event.getHubId())
                    .name(eventAvro.getName())
                    .conditions(new HashSet<>())
                    .actions(new HashSet<>())
                    .build();

            for (var item : eventAvro.getConditions()) {
                Set<Sensor> sensors = new HashSet<>();
                sensors.add(Sensor.builder().hubId(event.getHubId()).id(item.getSensorId()).build());

                scenario.addCondition(conditionRepository.saveAndFlush(
                        Condition.builder()
                            .type(item.getType().name())
                            .operation(item.getOperation().name())
                            .value(item.getValue())
                            .sensors(sensors)
                            .build()));
            }

            for (var item : eventAvro.getActions()) {
                Set<Sensor> sensors = new HashSet<>();
                sensors.add(Sensor.builder().hubId(event.getHubId()).id(item.getSensorId()).build());

                scenario.addAction(actionRepository.saveAndFlush(Action.builder()
                        .type(item.getType().name())
                        .value(item.getValue())
                        .sensors(sensors)
                        .build()));
            }

            scenarioRepository.save(scenario);

        } else if (event.getPayload().getClass().equals(ScenarioRemovedEventAvro.class)) {
            ScenarioRemovedEventAvro eventAvro = (ScenarioRemovedEventAvro) event.getPayload();

            scenarioRepository
                    .findByHubIdAndName(event.getHubId(), eventAvro.getName()).ifPresent(scenarioRepository::delete);
        }
    }
}
