package app.java.app.model;

import app.java.app.constant.Value;

import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ScenarioActionId implements Serializable {
    private Long scenarioId;

    private String sensorId;

    private Long actionId;

    @Override
    public String toString() {
        return String.format(Value.SCENARIO_ACTION_ID_STRING, scenarioId, sensorId, actionId);
    }
}
