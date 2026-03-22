package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.grpc.RpcClient;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LightSensorActionInterface implements ActionInterface {
    private final RpcClient client;

    @Override
    public void sendAction(Object obj, List<ConditionDto> conditionList, List<ActionDto> actionList) {
        LightSensorAvro sensor = (LightSensorAvro) obj;

        for (var item : conditionList) {
            String type = item.getCondition().getType();
            List<ActionDto> filteredActionList = actionList.stream()
                    .filter(a -> a.getSensor().getId().equals(item.getSensor().getId()) &&
                            a.getScenario().getName().equals(item.getScenario().getName())).toList();

            filteredActionList.forEach(a -> {
                if (ConditionTypeAvro.valueOf(type).equals(ConditionTypeAvro.LUMINOSITY)) {
                    if (compareValues(
                            item.getCondition().getOperation(),
                            item.getCondition().getValue(),
                            sensor.getLuminosity())) {
                        client.send(getDeviceActionRequest(a, item));
                    }
                }
            });
        }
    }

    @Override
    public Class getActionClass() {
        return LightSensorAvro.class;
    }

    @Override
    public String getType() {
        return DeviceTypeAvro.LIGHT_SENSOR.name();
    }
}
