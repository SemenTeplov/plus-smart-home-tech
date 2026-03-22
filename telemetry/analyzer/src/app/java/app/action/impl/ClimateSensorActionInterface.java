package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.grpc.RpcClient;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClimateSensorActionInterface implements ActionInterface {
    private final RpcClient client;

    @Override
    public void sendAction(Object obj, List<ConditionDto> conditionList, List<ActionDto> actionList) {
        ClimateSensorAvro sensor = (ClimateSensorAvro) obj;

        for (var item : conditionList) {
            String type = item.getCondition().getType();
            List<ActionDto> filteredActionList = actionList.stream()
                    .filter(a -> a.getSensor().getId().equals(item.getSensor().getId()) &&
                            a.getScenario().getName().equals(item.getScenario().getName())).toList();

            filteredActionList.forEach(a -> {
                switch (ConditionTypeAvro.valueOf(type)) {
                    case ConditionTypeAvro.TEMPERATURE -> {
                        if (compareValues(
                                item.getCondition().getOperation(),
                                item.getCondition().getValue(),
                                sensor.getTemperatureC())) {
                            client.send(getDeviceActionRequest(a, item));
                        }
                    }
                    case ConditionTypeAvro.HUMIDITY -> {
                        if (compareValues(
                                item.getCondition().getOperation(),
                                item.getCondition().getValue(),
                                sensor.getHumidity())) {
                            client.send(getDeviceActionRequest(a, item));
                        }
                    }
                    case ConditionTypeAvro.CO2LEVEL -> {
                        if (compareValues(
                                item.getCondition().getOperation(),
                                item.getCondition().getValue(),
                                sensor.getCo2Level())) {
                            client.send(getDeviceActionRequest(a, item));
                        }
                    }
                }
            });
        }
    }

    @Override
    public Class getActionClass() {
        return ClimateSensorAvro.class;
    }

    @Override
    public String getType() {
        return DeviceTypeAvro.CLIMATE_SENSOR.name();
    }
}
