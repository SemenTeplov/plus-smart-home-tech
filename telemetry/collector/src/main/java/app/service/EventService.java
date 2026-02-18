package app.service;

import app.dto.HubEvent;
import app.dto.SensorEvent;

public interface EventService {
    void collectSensorEvent(SensorEvent event);

    void collectHubEvent(HubEvent event);
}
