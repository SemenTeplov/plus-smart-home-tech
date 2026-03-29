package app.java.app.action.impl;

import app.java.app.action.ActionInterface;
import app.java.app.action.dto.ActionDto;
import app.java.app.action.dto.ConditionDto;
import app.java.app.constant.Message;
import app.java.app.grpc.RpcClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

import telemetry.messages.DeviceActionRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClimateSensorActionInterface implements ActionInterface {
    private final RpcClient client;

    @Override
    public void sendAction(Object obj, ConditionDto condition, ActionDto action) {
        log.info(Message.OBJECT_NAME, obj.getClass().getSimpleName());

        ClimateSensorAvro sensor = (ClimateSensorAvro) obj;

        String type = condition.getCondition().getType();

        switch (ConditionTypeAvro.valueOf(type)) {
            case ConditionTypeAvro.TEMPERATURE -> {
                log.info(Message.CHECK_PARAMETER, type);

                if (compareValues(
                        condition.getCondition().getOperation(),
                        sensor.getTemperatureC(),
                        condition.getCondition().getValue())) {
                    action.getAction().setValue(action.getAction().getValue() == 0 ? 1 : 0);
                }

                DeviceActionRequest request = getDeviceActionRequest(action, condition);

                log.info(Message.SEND_REQUEST,
                        request.getHubId(),
                        request.getScenarioName(),
                        request.getAction().getSensorId(),
                        request.getAction().getType(),
                        request.getAction().getValue());

                client.send(request);
            }
            case ConditionTypeAvro.HUMIDITY -> {
                log.info(Message.CHECK_PARAMETER, type);

                if (compareValues(
                        condition.getCondition().getOperation(),
                        sensor.getHumidity(),
                        condition.getCondition().getValue())) {

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
            case ConditionTypeAvro.CO2LEVEL -> {
                log.info(Message.CHECK_PARAMETER, type);

                if (compareValues(
                        condition.getCondition().getOperation(),
                        sensor.getCo2Level(),
                        condition.getCondition().getValue())) {

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
