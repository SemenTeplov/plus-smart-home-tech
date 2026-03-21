package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ActionRepository;
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
            ScenarioActionRepository scenarioActionRepository,
            ActionRepository actionRepository) {
        ClimateSensorAvro sensor = (ClimateSensorAvro) obj;
        String type = scenarioCondition.getCondition().getType();

        switch (ConditionTypeAvro.valueOf(type)) {
            case ConditionTypeAvro.TEMPERATURE -> save(
                    type,
                    scenarioCondition,
                    sensor.getTemperatureC(),
                    scenarioActionRepository,
                    actionRepository);
            case ConditionTypeAvro.HUMIDITY -> save(
                    type,
                    scenarioCondition,
                    sensor.getHumidity(),
                    scenarioActionRepository,
                    actionRepository);
            case ConditionTypeAvro.CO2LEVEL -> save(
                    type,
                    scenarioCondition,
                    sensor.getCo2Level(),
                    scenarioActionRepository,
                    actionRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return ClimateSensorAvro.class;
    }
}
