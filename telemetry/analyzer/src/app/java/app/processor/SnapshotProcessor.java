package app.java.app.processor;

import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ScenarioActionRepository;
import app.java.app.repository.ScenarioConditionRepository;
import app.java.app.action.ActionInterface;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

@Slf4j
@Service
public class SnapshotProcessor {
    private final ScenarioConditionRepository scenarioConditionRepository;

    private final ScenarioActionRepository scenarioActionRepository;

    private final List<ActionInterface> actions;

    @Autowired
    public SnapshotProcessor(
            List<ActionInterface> actions,
            ScenarioActionRepository scenarioActionRepository,
            ScenarioConditionRepository scenarioConditionRepository) {
        this.actions = actions;
        this.scenarioActionRepository = scenarioActionRepository;
        this.scenarioConditionRepository = scenarioConditionRepository;
    }

    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        List<ScenarioCondition> scenarioConditions =
                scenarioConditionRepository.findAll().stream()
                        .filter(s -> s.getScenario().getHubId().equals(event.getHubId())).toList();

        for (var scenario : scenarioConditions) {
            event.getSensorsState().values().forEach(o -> {
                actions.stream()
                        .filter(a -> o.getClass().equals(a.getActionClass()))
                        .forEach(a -> a.addAction(o, scenario, scenarioActionRepository));
            });
        }
    }
}
