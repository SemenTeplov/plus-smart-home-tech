package app.handler.HubEventImpl;

import app.handler.HubEventHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import telemetry.messages.HubEventProto;

@Slf4j
@Component
public class ScenarioAddedEventHandler implements HubEventHandler {

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info("ScenarioAdded: name: {}, conditions: {}, actions: {}",
                event.getScenarioAdded().getName(),
                event.getScenarioAdded().getConditionList(),
                event.getScenarioAdded().getActionList());
    }
}
