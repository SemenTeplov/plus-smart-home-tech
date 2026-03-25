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

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwitchSensorActionInterface implements ActionInterface {
    private final RpcClient client;

    @Override
    public void sendAction(Object obj, List<ConditionDto> conditionList, List<ActionDto> actionList) {
        log.info(Message.OBJECT_NAME, obj.getClass().getSimpleName());

        SwitchSensorAvro sensor = (SwitchSensorAvro) obj;

        for (var item : conditionList) {
            String type = item.getCondition().getType();

            actionList.stream().filter(a -> a.getScenario().equals(item.getScenario())).forEach(a -> {
                if (ConditionTypeAvro.valueOf(type).equals(ConditionTypeAvro.SWITCH)) {
                    log.info(Message.CHECK_PARAMETER, type);

                    if (compareValues(
                            item.getCondition().getOperation(),
                            item.getCondition().getValue(),
                            sensor.getState() ? 1 : 0)) {
                        DeviceActionRequest request = getDeviceActionRequest(a, item);

                        log.info(Message.SEND_REQUEST,
                                request.getHubId(),
                                request.getScenarioName(),
                                request.getAction().getSensorId(),
                                request.getAction().getType(),
                                request.getAction().getValue());

                        client.send(request);
                    }
                }
            });
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
