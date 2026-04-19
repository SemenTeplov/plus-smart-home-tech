package app.mapping;

import org.mapstruct.Mapper;

import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

import telemetry.messages.ClimateSensorProto;
import telemetry.messages.LightSensorProto;
import telemetry.messages.MotionSensorProto;
import telemetry.messages.SwitchSensorProto;
import telemetry.messages.TemperatureSensorProto;

@Mapper(componentModel = "spring")
public interface MappingSensorEvent {
    ClimateSensorAvro toClimateSensorAvro(ClimateSensorProto event);

    LightSensorAvro toLightSensorAvro(LightSensorProto event);

    MotionSensorAvro toMotionSensorAvro(MotionSensorProto event);

    SwitchSensorAvro toSwitchSensorAvro(SwitchSensorProto event);

    TemperatureSensorAvro toTemperatureSensorAvro(TemperatureSensorProto event);
}
