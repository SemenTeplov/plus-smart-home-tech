package app.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

import telemetry.messages.DeviceAddedEventProto;
import telemetry.messages.DeviceRemovedEventProto;
import telemetry.messages.ScenarioAddedEventProto;
import telemetry.messages.ScenarioConditionProto;
import telemetry.messages.ScenarioRemovedEventProto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MappingHubEvent {
    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEventProto event);

    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEventProto event);

    @Mapping(target = "conditions", source = "conditionsList", qualifiedByName = "toAvroContains")
    @Mapping(target = "actions", source = "actionsList")
    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEventProto event);

    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEventProto event);

    @Named("toAvroContains")
    default List<ScenarioConditionAvro> toAvroContains(List<ScenarioConditionProto> conditions) {
        if (conditions == null) {
            return null;
        }

        return conditions.stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setOperation(ConditionOperationAvro.valueOf(c.getOperation().name()))
                        .setType(ConditionTypeAvro.valueOf(c.getType().name()))
                        .setValue(c.hasBoolValue() ? 1 : c.getIntValue()).build())
                .toList();
    }
}
