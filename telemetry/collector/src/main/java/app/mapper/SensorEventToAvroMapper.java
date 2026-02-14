package app.mapper;

import app.dto.ClimateSensorEvent;

import app.dto.LightSensorEvent;
import app.dto.MotionSensorEvent;
import app.dto.SensorEvent;
import app.dto.SwitchSensorEvent;
import app.dto.TemperatureSensorEvent;

import org.mapstruct.Mapper;

import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Mapper(componentModel = "spring")
public interface SensorEventToAvroMapper {
    ClimateSensorAvro toClimateSensorAvro(ClimateSensorEvent climateSensorEvent);

    LightSensorAvro toLightSensorAvro(LightSensorEvent lightSensorEvent);

    MotionSensorAvro toMotionSensorAvro(MotionSensorEvent motionSensorEvent);

    SwitchSensorAvro toSwitchSensorAvro(SwitchSensorEvent switchSensorEvent);

    TemperatureSensorAvro toTemperatureSensorAvro(TemperatureSensorEvent temperatureSensorEvent);

    default SensorEventAvro map(SensorEvent event) {
        return new SensorEventAvro(event.getId(), event.getHubId(), event.getTimestamp(), mapPayload(event));
    }

    default Object mapPayload(SensorEvent event) {
        switch (event.getType()) {
            case CLIMATE_SENSOR_EVENT -> toClimateSensorAvro((ClimateSensorEvent) event);
            case LIGHT_SENSOR_EVENT -> toLightSensorAvro((LightSensorEvent) event);
            case MOTION_SENSOR_EVENT -> toMotionSensorAvro((MotionSensorEvent) event);
            case SWITCH_SENSOR_EVENT -> toSwitchSensorAvro((SwitchSensorEvent) event);
            case TEMPERATURE_SENSOR_EVENT -> toTemperatureSensorAvro((TemperatureSensorEvent) event);
        }

        return null;
    }
}
