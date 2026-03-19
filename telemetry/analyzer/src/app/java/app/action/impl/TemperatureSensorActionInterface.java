package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(Object obj, Condition condition, Scenario scenario, ScenarioRepository scenarioRepository) {
        TemperatureSensorAvro sensor = (TemperatureSensorAvro) obj;

        if (condition.getType().equals(ConditionTypeAvro.TEMPERATURE.name())) {
            save(condition, ConditionTypeAvro.TEMPERATURE.name(), scenario, sensor.getTemperatureC(), scenarioRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return TemperatureSensorAvro.class;
    }
}
