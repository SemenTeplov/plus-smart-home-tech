package app.handler.SensorEventImpl;

import lombok.extern.slf4j.Slf4j;

import app.handler.SensorEventHandler;

import org.springframework.stereotype.Component;

import telemetry.messages.SensorEventProto;

@Slf4j
@Component
public class TemperatureSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("TemperatureSensor: temperature c: {}, temperature f: {}",
                event.getTemperatureSensor().getTemperatureC(),
                event.getTemperatureSensor().getTemperatureF());
    }
}
