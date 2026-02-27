package app.mapping;

import org.mapstruct.Mapper;

import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import telemetry.messages.DeviceAddedEventProto;
import telemetry.messages.DeviceRemovedEventProto;
import telemetry.messages.ScenarioAddedEventProto;
import telemetry.messages.ScenarioRemovedEventProto;

@Mapper(componentModel = "spring")
public interface MappingHubEvent {
    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEventProto event);

    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEventProto event);

    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEventProto event);

    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEventProto event);
}
