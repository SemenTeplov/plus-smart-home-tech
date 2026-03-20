package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ScenarioActionRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(
            Object obj,
            ScenarioCondition scenarioCondition,
            ScenarioActionRepository scenarioActionRepository) {
        SwitchSensorAvro sensor = (SwitchSensorAvro) obj;
        String type = scenarioCondition.getCondition().getType();

        if (type.equals(ConditionTypeAvro.SWITCH.name())) {
            int switchSensor = sensor.getState() ? 1 : 0;

            save(ConditionTypeAvro.SWITCH.name(), scenarioCondition, switchSensor, scenarioActionRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return SwitchSensorAvro.class;
    }
}
