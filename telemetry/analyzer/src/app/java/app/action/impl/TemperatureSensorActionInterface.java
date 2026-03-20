package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ScenarioActionRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(
            Object obj,
            ScenarioCondition scenarioCondition,
            ScenarioActionRepository scenarioActionRepository) {
        TemperatureSensorAvro sensor = (TemperatureSensorAvro) obj;
        String type = scenarioCondition.getCondition().getType();

        if (type.equals(ConditionTypeAvro.TEMPERATURE.name())) {
            save(ConditionTypeAvro.TEMPERATURE.name(),
                    scenarioCondition,
                    sensor.getTemperatureC(),
                    scenarioActionRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return TemperatureSensorAvro.class;
    }
}
