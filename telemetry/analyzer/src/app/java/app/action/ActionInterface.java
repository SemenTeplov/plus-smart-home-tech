package app.java.app.action;

import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

public interface ActionInterface {
    void addAction(Object obj, Condition condition, Scenario scenario, ScenarioRepository scenarioRepository);

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

    default public void save(Condition condition, String type, Scenario scenario, Integer value, ScenarioRepository scenarioRepository) {
        if (condition.getType().equals(type) && compareValues(condition.getOperation(), condition.getValue(), value)) {
            scenario.addAction(app.java.app.model.Action.builder()
                    .type(type)
                    .value(value)
                    .build());

            scenarioRepository.save(scenario);
        }
    }
}
