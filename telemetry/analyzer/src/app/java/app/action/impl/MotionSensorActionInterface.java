package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.model.ScenarioCondition;
import app.java.app.repository.ScenarioActionRepository;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorActionInterface implements ActionInterface {
    @Override
    public void addAction(
            Object obj,
            ScenarioCondition scenarioCondition,
            ScenarioActionRepository scenarioActionRepository) {
        MotionSensorAvro sensor = (MotionSensorAvro) obj;
        String type = scenarioCondition.getCondition().getType();

        if (type.equals(ConditionTypeAvro.MOTION.name())) {
            int motion = sensor.getMotion() ? 1 : 0;

            save(ConditionTypeAvro.MOTION.name(), scenarioCondition, motion, scenarioActionRepository);
        }
    }

    @Override
    public Class getActionClass() {
        return MotionSensorAvro.class;
    }
}
