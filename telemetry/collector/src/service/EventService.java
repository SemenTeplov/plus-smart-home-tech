package service;

import dto.HubEvent;
import dto.SensorEvent;

import org.springframework.stereotype.Service;

@Service
public interface EventService {
    void collectSensorEvent(SensorEvent event);

    void collectHubEvent(HubEvent event);
}
