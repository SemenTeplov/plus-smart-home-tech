package app.java.app.action;

import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;

import telemetry.messages.ActionTypeProto;
import telemetry.messages.DeviceActionProto;
import telemetry.messages.DeviceActionRequest;

public interface ActionInterface {
    void sendAction(Object obj, ConditionDto condition, ActionDto action);

    Class getActionClass();

    String getType();

    default public boolean compareValues(String operator, Integer value, Integer other) {
        return switch (ConditionOperationAvro.valueOf(operator)) {
            case EQUALS -> value.equals(other);
            case GREATER_THAN -> value > other;
            case LOWER_THAN -> value < other;
            default -> false;
        };
    }

    default public DeviceActionRequest getDeviceActionRequest(ActionDto action, ConditionDto condition) {
        DeviceActionProto deviceActionProto = DeviceActionProto.newBuilder()
                .setSensorId(action.getSensor().getId())
                .setTypeValue(ActionTypeProto.valueOf(action.getAction().getType()).getNumber())
                .setValue(action.getAction().getValue())
                .build();

        return DeviceActionRequest.newBuilder()
                .setHubId(condition.getSensor().getHubId())
                .setScenarioName(action.getScenario().getName())
                .setAction(deviceActionProto)
                .build();
    }
}
