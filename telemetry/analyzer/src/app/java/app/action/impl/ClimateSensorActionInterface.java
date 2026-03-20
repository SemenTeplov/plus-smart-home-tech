package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ScenarioActionRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Component
public class ClimateSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(
            Object obj,
            ScenarioCondition scenarioCondition,
            ScenarioActionRepository scenarioActionRepository) {
        ClimateSensorAvro sensor = (ClimateSensorAvro) obj;
        String type = scenarioCondition.getCondition().getType();

        if (type.equals(ConditionTypeAvro.TEMPERATURE.name())) {
            save(ConditionTypeAvro.TEMPERATURE.name(),
                    scenarioCondition,
                    sensor.getTemperatureC(),
                    scenarioActionRepository);
        }

        if (type.equals(ConditionTypeAvro.HUMIDITY.name())) {
            save(ConditionTypeAvro.HUMIDITY.name(),
                    scenarioCondition,
                    sensor.getHumidity(),
                    scenarioActionRepository);
        }

        if (type.equals(ConditionTypeAvro.CO2LEVEL.name())) {
            save(ConditionTypeAvro.CO2LEVEL.name(),
                    scenarioCondition,
                    sensor.getCo2Level(),
                    scenarioActionRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return ClimateSensorAvro.class;
    }
}
