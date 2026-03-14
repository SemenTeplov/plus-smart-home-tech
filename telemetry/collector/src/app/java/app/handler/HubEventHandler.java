package app.handler;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import telemetry.messages.HubEventProto;

import java.time.Instant;

public interface HubEventHandler {
    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);

    default public ProducerRecord<String, SpecificRecordBase> getRecord(
            HubEventProto event, SpecificRecordBase base, String topic) {
        return new ProducerRecord<>(
                topic,
                null,
                HubEventAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant
                                .ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                        .setPayload(base).build());
    }
}
