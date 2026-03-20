package app.java.app.action;

import app.java.app.model.Action;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.model.ScenarioAction;
import app.java.app.model.ScenarioActionId;
import app.java.app.model.ScenarioCondition;
import app.java.app.model.Sensor;
import app.java.app.repository.ScenarioActionRepository;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

public interface ActionInterface {
    void addAction(Object obj, ScenarioCondition scenarioCondition, ScenarioActionRepository scenarioActionRepository);

    Class getActionClass();

    default public boolean compareValues(String operator, Integer value, Integer other) {
        if (ConditionOperationAvro.EQUALS.name().equals(operator)) {
            return value.equals(other);
        } else if (ConditionOperationAvro.GREATER_THAN.name().equals(operator)) {
            return value > other;
        } else if (ConditionOperationAvro.LOWER_THAN.name().equals(operator)) {
            return value < other;
        }

        return false;
    }

    default public void save(String type,
                             ScenarioCondition scenarioCondition,
                             int value,
                             ScenarioActionRepository scenarioActionRepository) {
        Condition condition = scenarioCondition.getCondition();

        if (condition.getType().equals(type) &&
                compareValues(condition.getOperation(), condition.getValue(), value)) {
            Scenario scenario = scenarioCondition.getScenario();
            Sensor sensor = scenarioCondition.getSensor();
            Action action = Action.builder().type(type).value(value).build();

            ScenarioAction scenarioAction = ScenarioAction.builder()
                    .sensor(sensor)
                    .scenario(scenario)
                    .action(action)
                    .id(new ScenarioActionId(scenario.getId(), sensor.getId(), action.getId()))
                    .build();

            scenarioActionRepository.save(scenarioAction);
        }
    }
}
