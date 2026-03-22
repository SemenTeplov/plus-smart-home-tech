package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.grpc.RpcClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import telemetry.messages.DeviceActionRequest;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateSensorActionInterface implements ActionInterface {
    private final RpcClient client;

    @Override
    public void sendAction(Object obj, List<ConditionDto> conditionList, List<ActionDto> actionList) {
        log.info("Объектом является ClimateSensorAvro");

        ClimateSensorAvro sensor = (ClimateSensorAvro) obj;

        for (var item : conditionList) {
            String type = item.getCondition().getType();

            actionList.forEach(a -> {
                log.info("Temperature соответствует {}", ConditionTypeAvro.valueOf(type).equals(ConditionTypeAvro.TEMPERATURE));
                switch (ConditionTypeAvro.valueOf(type)) {
                    case ConditionTypeAvro.TEMPERATURE -> {
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
                    case ConditionTypeAvro.HUMIDITY -> {
                        log.info("Проверка Humidity");

                        if (compareValues(
                                item.getCondition().getOperation(),
                                item.getCondition().getValue(),
                                sensor.getHumidity())) {
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
                    case ConditionTypeAvro.CO2LEVEL -> {
                        log.info("Проверка Co2Level");

                        if (compareValues(
                                item.getCondition().getOperation(),
                                item.getCondition().getValue(),
                                sensor.getCo2Level())) {
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
