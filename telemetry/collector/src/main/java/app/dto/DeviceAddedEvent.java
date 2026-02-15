package app.dto;

import app.persistence.status.DeviceType;
import app.persistence.status.HubEventType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeviceAddedEvent extends HubEvent {
    @NonNull
    String id;

    @NonNull
    DeviceType deviceType;

    @Override
    public HubEventType getHubType() {
        return HubEventType.DEVICE_ADDED;
    }
}
