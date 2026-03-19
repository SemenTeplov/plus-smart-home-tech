package app.java.app.service;

import app.java.app.grpc.RpcClient;

import app.java.app.repository.ScenarioRepository;
import app.java.app.repository.SensorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import telemetry.messages.ActionTypeProto;
import telemetry.messages.DeviceActionProto;
import telemetry.messages.DeviceActionRequest;

@Service
public class EventService {
    private final RpcClient client;

    private final ScenarioRepository scenarioRepository;

    private final SensorRepository sensorRepository;

    @Autowired
    public EventService(
            RpcClient client,
            ScenarioRepository scenarioRepository,
            SensorRepository sensorRepository) {
        this.client = client;
        this.scenarioRepository = scenarioRepository;
        this.sensorRepository = sensorRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void handler() {
        scenarioRepository.findAll().forEach(s -> {
            s.getActions().forEach(a -> {
                client.send(DeviceActionRequest.newBuilder()
                        .setHubId(s.getHubId())
                        .setScenarioName(s.getName())
                        .setAction(DeviceActionProto.newBuilder()
                                .setSensorId(sensorRepository.findByActionId(a.getId()).get().getId())
                                .setTypeValue(
                                        ActionTypeProto
                                                .valueOf(a.getType()).getNumber())
                                                .setValue(a.getValue())
                                                .build()).build());
            });
        });
    }
}
