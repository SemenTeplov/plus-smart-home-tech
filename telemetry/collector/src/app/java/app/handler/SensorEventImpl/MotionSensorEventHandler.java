package app.handler.SensorEventImpl;

import app.constants.Messages;
import app.handler.SensorEventHandler;
import app.mapping.MappingSensorEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telemetry.messages.SensorEventProto;

@Slf4j
@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {
    private final MappingSensorEvent mapping;

    private final Producer<String, SpecificRecordBase> eventProducer;

    @Value("${collector.kafka.topics.sensor}")
    private String topic;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info(Messages.MESSAGE_SEND_SENSOR, event);

        eventProducer.send(getRecord(event, mapping.toMotionSensorAvro(event.getMotionSensor()), topic));
    }
}
