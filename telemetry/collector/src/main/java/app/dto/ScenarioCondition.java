package app.dto;

import lombok.Builder;

import app.persistence.status.ConditionType;
import app.persistence.status.OperationType;

@Builder
public record ScenarioCondition(
    String sensorId,
    ConditionType type,
    OperationType operation,
    Boolean value
) {

}
