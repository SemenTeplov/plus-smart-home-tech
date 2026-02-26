package app.handler.HubEventImpl;

import app.handler.HubEventHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import telemetry.messages.HubEventProto;

@Slf4j
@Component
public class DeviceAddedEventHandler implements HubEventHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("DeviceAdd: id: {}, type: {}",
                event.getDeviceAdded().getId(),
                event.getDeviceAdded().getType());
    }
}
