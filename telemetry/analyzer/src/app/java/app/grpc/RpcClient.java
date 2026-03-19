package app.java.app.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.stereotype.Service;

import telemetry.messages.DeviceActionRequest;
import telemetry.service.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

@Service
public class RpcClient {
    private final HubRouterControllerBlockingStub hubRouterClient;

    public RpcClient(@GrpcClient("hub-router") HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void send(DeviceActionRequest request) {
        hubRouterClient.handleDeviceAction(request);
    }
}
