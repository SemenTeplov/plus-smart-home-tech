package app.handler.SensorEventImpl;

import app.handler.SensorEventHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import telemetry.messages.SensorEventProto;

@Slf4j
@Component
public class SwitchSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("SwitchSensor: state: {}",
                event.getSwitchSensor().getState());
    }
}
