package app.handler.SensorEventImpl;

import app.handler.SensorEventHandler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import telemetry.messages.SensorEventProto;

@Slf4j
@Component
public class LightSensorEventHandler implements SensorEventHandler {
    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("LightSensor: link quality: {}, luminosity: {}",
                event.getLightSensor().getLinkQuality(),
                event.getLightSensor().getLuminosity());
    }
}
