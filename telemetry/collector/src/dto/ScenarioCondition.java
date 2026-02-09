package dto;

import lombok.Builder;

import persistence.status.ConditionType;
import persistence.status.OperationType;

@Builder
public record ScenarioCondition(
    String sensorId,
    ConditionType type,
    OperationType operation,
    Integer value
) {

}
