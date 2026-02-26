package app.handler.SensorEventImpl;

import app.handler.SensorEventHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import telemetry.messages.SensorEventProto;

@Slf4j
@Component
public class ClimateSensorEventHandler implements SensorEventHandler {

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.CLIMATE_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info("ClimateSensor: temperature c: {}, humidity: {}, co2 level: {}",
                event.getClimateSensor().getTemperatureC(),
                event.getClimateSensor().getHumidity(),
                event.getClimateSensor().getCo2Level());
    }
}
