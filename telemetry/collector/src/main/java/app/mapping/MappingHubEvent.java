package app.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import telemetry.messages.DeviceAddedEventProto;
import telemetry.messages.DeviceRemovedEventProto;
import telemetry.messages.ScenarioAddedEventProto;
import telemetry.messages.ScenarioRemovedEventProto;

@Mapper(componentModel = "spring")
public interface MappingHubEvent {
    @Mapping(target = "type", source = "type")
    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEventProto event);

    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEventProto event);

    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEventProto event);

    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEventProto event);

    default DeviceTypeAvro map(DeviceAddedEventProto event) {
        return DeviceTypeAvro.valueOf(event.getType().toString());
    }
}
