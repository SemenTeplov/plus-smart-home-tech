package app.handler.HubEventImpl;

import app.handler.HubEventHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import telemetry.messages.HubEventProto;

@Slf4j
@Component
public class ScenarioRemovedEventHandler implements HubEventHandler {
    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("ScenarioRemoved: name: {}",
                event.getScenarioAdded().getName());
    }
}
