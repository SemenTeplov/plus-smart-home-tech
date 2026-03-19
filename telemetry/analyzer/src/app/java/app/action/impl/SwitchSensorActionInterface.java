package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(Object obj, Condition condition, Scenario scenario, ScenarioRepository scenarioRepository) {
        SwitchSensorAvro sensor = (SwitchSensorAvro) obj;

        if (condition.getType().equals(ConditionTypeAvro.SWITCH.name())) {
            int switchSensor = sensor.getState() ? 1 : 0;

            save(condition, ConditionTypeAvro.SWITCH.name(), scenario, switchSensor, scenarioRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return SwitchSensorAvro.class;
    }
}
