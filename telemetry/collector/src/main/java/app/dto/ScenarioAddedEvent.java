package app.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import app.persistence.status.HubEventType;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ScenarioAddedEvent extends HubEvent {
    @NonNull
    String name;

    @NonNull
    List<ScenarioCondition> conditions;

    @NonNull
    List<DeviceAction> actions;

    @Override
    public HubEventType getHubType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
