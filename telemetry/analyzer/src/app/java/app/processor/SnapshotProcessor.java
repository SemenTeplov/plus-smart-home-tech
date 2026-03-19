package app.java.app.processor;

import app.java.app.model.Action;
import app.java.app.model.Condition;
import app.java.app.model.Scenario;
import app.java.app.repository.ScenarioRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import java.util.List;

@Slf4j
@Service
public class SnapshotProcessor {
    private final ScenarioRepository scenarioRepository;

    @Autowired
    public SnapshotProcessor(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    @KafkaListener(topics = "${kafka.topics.snapshot}", containerFactory = "snapshotConsumer")
    public void handler(SensorsSnapshotAvro event) {
        List<Scenario> scenarios = scenarioRepository.findByHubId(event.getHubId());

        for (var scenario : scenarios) {
            for (var condition : scenario.getConditions()) {
                event.getSensorsState().values()
                        .forEach(o -> addAction(o, condition, scenario));
            }
        }
    }

    private void addAction(Object obj, Condition condition, Scenario scenario) {
        if (obj.getClass().equals(ClimateSensorAvro.class)) {
            ClimateSensorAvro sensor = (ClimateSensorAvro) obj;

            if (condition.getType().equals(ConditionTypeAvro.TEMPERATURE.name())) {
                save(condition, ConditionTypeAvro.TEMPERATURE.name(), scenario, sensor.getTemperatureC());
            }

            if (condition.getType().equals(ConditionTypeAvro.HUMIDITY.name())) {
                save(condition, ConditionTypeAvro.HUMIDITY.name(), scenario, sensor.getHumidity());
            }

            if (condition.getType().equals(ConditionTypeAvro.CO2LEVEL.name())) {
                save(condition, ConditionTypeAvro.CO2LEVEL.name(), scenario, sensor.getCo2Level());
            }
        }

        if (obj.getClass().equals(LightSensorAvro.class)) {
            LightSensorAvro sensor = (LightSensorAvro) obj;

            if (condition.getType().equals(ConditionTypeAvro.LUMINOSITY.name())) {
                save(condition, ConditionTypeAvro.LUMINOSITY.name(), scenario, sensor.getLuminosity());
            }
        }

        if (obj.getClass().equals(MotionSensorAvro.class)) {
            MotionSensorAvro sensor = (MotionSensorAvro) obj;

            if (condition.getType().equals(ConditionTypeAvro.MOTION.name())) {
                int motion = sensor.getMotion() ? 1 : 0;

                save(condition, ConditionTypeAvro.MOTION.name(), scenario, motion);
            }
        }

        if (obj.getClass().equals(SwitchSensorAvro.class)) {
            SwitchSensorAvro sensor = (SwitchSensorAvro) obj;

            if (condition.getType().equals(ConditionTypeAvro.SWITCH.name())) {
                int switchSensor = sensor.getState() ? 1 : 0;

                save(condition, ConditionTypeAvro.SWITCH.name(), scenario, switchSensor);
            }
        }

        if (obj.getClass().equals(TemperatureSensorAvro.class)) {
            TemperatureSensorAvro sensor = (TemperatureSensorAvro) obj;

            if (condition.getType().equals(ConditionTypeAvro.TEMPERATURE.name())) {
                save(condition, ConditionTypeAvro.TEMPERATURE.name(), scenario, sensor.getTemperatureC());
            }
        }
    }

    private boolean compareValues(String operator, Integer value, Integer other) {
        if (ConditionOperationAvro.EQUALS.name().equals(operator)) {
            return value.equals(other);
        } else if (ConditionOperationAvro.GREATER_THAN.name().equals(operator)) {
            return value > other;
        } else if (ConditionOperationAvro.LOWER_THAN.name().equals(operator)) {
            return value < other;
        }

        return false;
    }

    private void save(Condition condition, String type, Scenario scenario, Integer value) {
        if (condition.getType().equals(type) && compareValues(condition.getOperation(), condition.getValue(), value)) {
            scenario.addAction(Action.builder()
                    .type(type)
                    .value(value)
                    .build());

            scenarioRepository.save(scenario);
        }
    }
}
