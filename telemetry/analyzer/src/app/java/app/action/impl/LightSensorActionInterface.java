package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ActionRepository;
import app.java.app.repository.ScenarioActionRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(
            Object obj,
            ScenarioCondition scenarioCondition,
            ScenarioActionRepository scenarioActionRepository,
            ActionRepository actionRepository) {
        LightSensorAvro sensor = (LightSensorAvro) obj;
        String type = scenarioCondition.getCondition().getType();

        if (ConditionTypeAvro.valueOf(type).equals(ConditionTypeAvro.LUMINOSITY)) {
            save(type, scenarioCondition, sensor.getLuminosity(), scenarioActionRepository, actionRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return LightSensorAvro.class;
    }
}
