package app.java.app.service;

import app.java.app.grpc.RpcClient;
import app.java.app.repository.ActionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import telemetry.messages.ActionTypeProto;
import telemetry.messages.DeviceActionProto;
import telemetry.messages.DeviceActionRequest;

@Service
public class EventService {
    private final RpcClient client;

    private final ActionRepository actionRepository;

    @Autowired
    public EventService(RpcClient client, ActionRepository actionRepository) {
        this.client = client;
        this.actionRepository = actionRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void handler() {
        actionRepository.findAll().forEach(i -> {
            client.send(DeviceActionRequest.newBuilder()
                    .setHubId(i.getSensors().stream().findAny().get().getHubId())
                    .setScenarioName(i.getScenarios().stream().findAny().get().getName())
                    .setAction(DeviceActionProto.newBuilder()
                            .setSensorId(i.getSensors().stream().findAny().get().getId())
                            .setTypeValue(ActionTypeProto.valueOf(i.getType()).getNumber())
                            .setValue(i.getValue())
                            .build()).build());
        });
    }
}
