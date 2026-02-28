package app.handler.SensorEventImpl;

import app.constants.Messages;
import app.handler.SensorEventHandler;
import app.mapping.MappingSensorEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import telemetry.messages.SensorEventProto;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {
    private final MappingSensorEvent mapping;

    private final Producer<String, SpecificRecordBase> eventProducer;

    @Value("${kafka.topics.sensor}")
    private String topic;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR;
    }

    @Override
    public void handle(SensorEventProto event) {
        log.info(Messages.MESSAGE_SEND_SENSOR, event);

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                SensorEventAvro.newBuilder()
                        .setId(event.getId())
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant
                                .ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                        .setPayload(mapping.toLightSensorAvro(event.getLightSensor())).build());

        eventProducer.send(record);
    }
}
