package app.java.app.processor;

import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;
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
    private final ScenarioRepository scenarioRepository;

    private final List<ActionInterface> actions;

    @Autowired
    public SnapshotProcessor(ScenarioRepository scenarioRepository, List<ActionInterface> actions) {
        this.scenarioRepository = scenarioRepository;
        this.actions = actions;
    }

    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(event.getHubId());

        for (var scenario : scenarios) {
            for (var condition : scenario.getConditions()) {
                event.getSensorsState().values()
                        .forEach(o -> {
                            actions.stream()
                                    .filter(a -> o.getClass().equals(a.getActionClass()))
                                    .forEach(a -> a.addAction(o, condition, scenario, scenarioRepository));
                        });
            }
        }
    }
}
