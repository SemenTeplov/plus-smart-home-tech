package app.java.app.processor;

import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.action.ActionInterface;
import app.java.app.constant.Message;
import app.java.app.repository.ScenarioActionRepository;
import app.java.app.repository.ScenarioConditionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final List<ActionInterface> actions;

    private final ScenarioConditionRepository scenarioConditionRepository;

    private final ScenarioActionRepository scenarioActionRepository;

    @Transactional
    @KafkaListener(topics = "${analyzer.kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event, Acknowledgment acknowledgment) {
        log.info(Message.GET_SENSORS_SNAPSHOT, event.getHubId());

        event.getSensorsState().entrySet()
            .forEach(e -> {
                scenarioConditionRepository.findAll().stream()
                        .filter(sc -> sc.getSensor().getId().equals(e.getKey()))
                        .map(sc ->
                                ConditionDto.builder()
                                    .scenario(sc.getScenario())
                                    .condition(sc.getCondition())
                                    .sensor(sc.getSensor()).build())
                        .toList().forEach(
                        sc -> {
                            scenarioActionRepository.findAll().stream()
                                    .filter(sa -> sa.getScenario().equals(sc.getScenario()))
                                    .map(sa ->
                                            ActionDto.builder()
                                                    .action(sa.getAction())
                                                    .scenario(sa.getScenario())
                                                    .sensor(sa.getSensor())
                                                    .build())
                                    .toList().forEach(sa -> {
                                        actions.stream()
                                                .filter(a -> a.getActionClass().getName()
                                                        .equals(e.getValue().getData().getClass().getName()))
                                                .forEach(a -> {
                                                    a.sendAction(e.getValue().getData(), sc, sa);
                                                });
                                    });
                        });
            });
        acknowledgment.acknowledge();
    }
}
