package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(Object obj, Condition condition, Scenario scenario, ScenarioRepository scenarioRepository) {
        MotionSensorAvro sensor = (MotionSensorAvro) obj;

        if (condition.getType().equals(ConditionTypeAvro.MOTION.name())) {
            int motion = sensor.getMotion() ? 1 : 0;

            save(condition, ConditionTypeAvro.MOTION.name(), scenario, motion, scenarioRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return MotionSensorAvro.class;
    }
}
