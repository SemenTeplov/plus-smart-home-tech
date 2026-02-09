package dto;

import lombok.Builder;

import persistence.status.ActionType;

@Builder
public record DeviceAction(
        String sensorId,
        ActionType type,
        Integer value
) {

}
