package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.grpc.RpcClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import telemetry.messages.DeviceActionRequest;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemperatureSensorActionInterface implements ActionInterface {
    private final RpcClient client;

    @Override
    public void sendAction(Object obj, List<ConditionDto> conditionList, List<ActionDto> actionList) {
        log.info("Объектом является TemperatureSensorAvro");

        TemperatureSensorAvro sensor = (TemperatureSensorAvro) obj;

        for (var item : conditionList) {
            String type = item.getCondition().getType();
            List<ActionDto> filteredActionList = actionList.stream()
                    .filter(a -> a.getSensor().getId().equals(item.getSensor().getId()) &&
                            a.getScenario().getName().equals(item.getScenario().getName())).toList();

            filteredActionList.forEach(a -> {
                if (ConditionTypeAvro.valueOf(type).equals(ConditionTypeAvro.TEMPERATURE)) {
                    log.info("Проверка Temperature");

                    if (compareValues(
                            item.getCondition().getOperation(),
                            item.getCondition().getValue(),
                            sensor.getTemperatureC())) {
                        DeviceActionRequest request = getDeviceActionRequest(a, item);

                        log.info("Отправлен DeviceActionRequest: " +
                                        "HubId - {}, " +
                                        "ScenarioName - {}," +
                                        "SensorId - {}," +
                                        "TypeValue - {}," +
                                        "Value - {}",
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
        return TemperatureSensorAvro.class;
    }

    @Override
    public String getType() {
        return DeviceTypeAvro.TEMPERATURE_SENSOR.name();
    }
}
