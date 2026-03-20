package app.java.app.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sensors")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sensor {
    @Id
    String id;

    String hubId;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ScenarioCondition> conditions = new HashSet<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ScenarioAction> actions = new HashSet<>();

    public void addCondition(ScenarioCondition condition) {
        conditions.add(condition);
        condition.setSensor(this);
    }

    public void addAction(ScenarioAction action) {
        actions.add(action);
        action.setSensor(this);
    }

    @Override
    public String toString() {
        return String.format("id: %s, hubId: %s", id, hubId);
    }
}
