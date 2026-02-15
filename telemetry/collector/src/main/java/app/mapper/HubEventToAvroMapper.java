package app.mapper;

import app.dto.DeviceAddedEvent;
import app.dto.DeviceRemovedEvent;
import app.dto.HubEvent;
import app.dto.ScenarioAddedEvent;
import app.dto.ScenarioRemovedEvent;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Mapper(componentModel = "spring")
public interface HubEventToAvroMapper {

    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEvent deviceAddedEvent);

    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEvent deviceRemovedEvent);

    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEvent scenarioAddedEvent);

    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEvent scenarioRemovedEvent);

    default HubEventAvro map(HubEvent event) {
        return new HubEventAvro(event.getHubId(), event.getTimestamp(), mapPayload(event));
    }

    default Object mapPayload(HubEvent event) {
        return switch (event.getHubType()) {
            case DEVICE_ADDED -> toDeviceAddedEventAvro((DeviceAddedEvent) event);
            case DEVICE_REMOVED -> toDeviceRemovedEventAvro((DeviceRemovedEvent) event);
            case SCENARIO_ADDED -> toScenarioAddedEventAvro((ScenarioAddedEvent) event);
            case SCENARIO_REMOVED -> toScenarioRemovedEventAvro((ScenarioRemovedEvent) event);
        };
    }
}
