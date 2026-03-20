package app.java.app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "scenario_conditions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioCondition {
    @EmbeddedId
    private ScenarioConditionId id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @MapsId("scenarioId")
    @JoinColumn(name = "scenario_id", referencedColumnName = "id")
    private Scenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sensorId")
    @JoinColumn(name = "sensor_id", referencedColumnName = "id")
    private Sensor sensor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conditionId")
    @JoinColumn(name = "condition_id", referencedColumnName = "id")
    private Condition condition;

    @Override
    public String toString() {
        return String.format("id: %s", id);
    }
}
