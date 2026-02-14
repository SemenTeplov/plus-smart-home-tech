package app.dto;

import lombok.Builder;

import app.persistence.status.ActionType;

@Builder
public record DeviceAction(
        String sensorId,
        ActionType type,
        Integer value
) {

}
