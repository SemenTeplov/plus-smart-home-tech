package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(Object obj, Condition condition, Scenario scenario, ScenarioRepository scenarioRepository) {
        LightSensorAvro sensor = (LightSensorAvro) obj;

        if (condition.getType().equals(ConditionTypeAvro.LUMINOSITY.name())) {
            save(condition, ConditionTypeAvro.LUMINOSITY.name(), scenario, sensor.getLuminosity(), scenarioRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return LightSensorAvro.class;
    }
}
