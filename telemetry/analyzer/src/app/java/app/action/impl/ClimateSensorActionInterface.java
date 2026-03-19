package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Component
public class ClimateSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(Object obj, Condition condition, Scenario scenario, ScenarioRepository scenarioRepository) {
        ClimateSensorAvro sensor = (ClimateSensorAvro) obj;

        if (condition.getType().equals(ConditionTypeAvro.TEMPERATURE.name())) {
            save(condition, ConditionTypeAvro.TEMPERATURE.name(), scenario, sensor.getTemperatureC(), scenarioRepository);
        }

        if (condition.getType().equals(ConditionTypeAvro.HUMIDITY.name())) {
            save(condition, ConditionTypeAvro.HUMIDITY.name(), scenario, sensor.getHumidity(), scenarioRepository);
        }

        if (condition.getType().equals(ConditionTypeAvro.CO2LEVEL.name())) {
            save(condition, ConditionTypeAvro.CO2LEVEL.name(), scenario, sensor.getCo2Level(), scenarioRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return ClimateSensorAvro.class;
    }
}
