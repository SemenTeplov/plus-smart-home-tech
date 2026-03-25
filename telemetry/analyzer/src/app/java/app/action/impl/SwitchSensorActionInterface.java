package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.constant.Message;
import app.java.app.grpc.RpcClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

import telemetry.messages.DeviceActionRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwitchSensorActionInterface implements ActionInterface {
    private final RpcClient client;

    @Override
    public void sendAction(Object obj, ConditionDto condition, ActionDto action) {
        log.info(Message.OBJECT_NAME, obj.getClass().getSimpleName());

        SwitchSensorAvro sensor = (SwitchSensorAvro) obj;

        String type = condition.getCondition().getType();

        if (ConditionTypeAvro.valueOf(type).equals(ConditionTypeAvro.SWITCH)) {
            log.info(Message.CHECK_PARAMETER, type);

            if (compareValues(
                    condition.getCondition().getOperation(),
                    condition.getCondition().getValue(),
                    sensor.getState() ? 1 : 0)) {
                DeviceActionRequest request = getDeviceActionRequest(action, condition);

                log.info(Message.SEND_REQUEST,
                        request.getHubId(),
                        request.getScenarioName(),
                        request.getAction().getSensorId(),
                        request.getAction().getType(),
                        request.getAction().getValue());

                client.send(request);
            }
        }
    }

    @Override
    public Class getActionClass() {
        return SwitchSensorAvro.class;
    }

    @Override
    public String getType() {
        return DeviceTypeAvro.SWITCH_SENSOR.name();
    }
}
