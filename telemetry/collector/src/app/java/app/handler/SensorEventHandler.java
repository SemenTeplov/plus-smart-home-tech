package app.handler;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import telemetry.messages.SensorEventProto;

import java.time.Instant;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);

    default public ProducerRecord<String, SpecificRecordBase> getRecord(
            SensorEventProto event, SpecificRecordBase base, String topic) {
        return new ProducerRecord<>(
                topic,
                null,
                SensorEventAvro.newBuilder()
                        .setId(event.getId())
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant
                                .ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                        .setPayload(base).build());
    }
}
