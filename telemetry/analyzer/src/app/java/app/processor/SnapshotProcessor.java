package app.java.app.processor;

import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.model.Scenario;
import app.java.app.action.ActionInterface;
import app.java.app.repository.ScenarioRepository;

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
    private final ScenarioRepository scenarioRepository;

    private final List<ActionInterface> actions;

    @Autowired
    public SnapshotProcessor(
            List<ActionInterface> actions,
            ScenarioRepository scenarioRepository) {
        this.actions = actions;
        this.scenarioRepository = scenarioRepository;
    }

    @Transactional
    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        log.info("Поступил SensorsSnapshotAvro с HubId: {}", event.getHubId());

        List<Scenario> scenarios = scenarioRepository.findByHubId(event.getHubId());

        List<ConditionDto> conditionsDto = scenarios.stream()
                .flatMap(s -> s.getConditions().stream()
                        .map(c -> ConditionDto.builder()
                                .scenario(s)
                                .condition(c.getCondition())
                                .sensor(c.getSensor()).build()))
                .toList();

        List<ActionDto> actionsDto = scenarios.stream()
                .flatMap(s -> s.getActions().stream()
                        .map(a -> ActionDto.builder()
                                .scenario(s)
                                .sensor(a.getSensor())
                                .action(a.getAction()).build()))
                .toList();

        event.getSensorsState().values().forEach(o -> {
                actions.stream()
                        .filter(a -> o.getClass().equals(a.getActionClass()))
                        .forEach(a -> a.sendAction(o,
                                conditionsDto.stream()
                                        .filter(c -> c.getSensor().getId().equals(a.getType())).toList(),
                                actionsDto.stream()
                                        .filter(c -> c.getSensor().getId().equals(a.getType())).toList()));
            });
    }
}
