package app.java.app.service;

import app.java.app.repository.ActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import telemetry.messages.ActionTypeProto;
import telemetry.messages.DeviceActionProto;
import telemetry.messages.DeviceActionRequest;
import telemetry.service.hubrouter.HubRouterControllerGrpc;

import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class EventService {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private final ActionRepository actionRepository;

    @Autowired
    public EventService(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient,
                        ActionRepository actionRepository) {
        this.hubRouterClient = hubRouterClient;
        this.actionRepository = actionRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void handler() {
        actionRepository.findAll().forEach(i -> {
            hubRouterClient.handleDeviceAction(DeviceActionRequest.newBuilder()
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
