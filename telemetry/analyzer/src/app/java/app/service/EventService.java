package app.java.app.service;

import app.java.app.grpc.RpcClient;

import app.java.app.repository.ScenarioActionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import telemetry.messages.ActionTypeProto;
import telemetry.messages.DeviceActionProto;
import telemetry.messages.DeviceActionRequest;

@Service
@RequiredArgsConstructor
public class EventService {
    private final RpcClient client;

    private final ScenarioActionRepository scenarioActionRepository;

    @Scheduled(fixedDelay = 5000)
    public void handler() {
        scenarioActionRepository.findAll().forEach(s -> {
            client.send(DeviceActionRequest.newBuilder()
                    .setHubId(s.getSensor().getHubId())
                    .setScenarioName(s.getScenario().getName())
                    .setAction(DeviceActionProto.newBuilder()
                            .setSensorId(s.getSensor().getId())
                            .setTypeValue(ActionTypeProto.valueOf(s.getAction().getType()).getNumber())
                            .setValue(s.getAction().getValue())
                            .build())
                    .build());
        });
    }
}
