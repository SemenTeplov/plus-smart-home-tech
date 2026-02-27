package app.handler.HubEventImpl;

import app.constants.Messages;
import app.handler.HubEventHandler;
import app.mapping.MappingHubEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import telemetry.messages.HubEventProto;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final MappingHubEvent mapping;

    private final Producer<String, SpecificRecordBase> eventProducer;

    @Value("${kafka.topics.hub}")
    private String topic;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info(Messages.MESSAGE_SEND_HUB, event);

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                HubEventAvro.newBuilder()
                        .setHubId(event.getHubId())
                        .setTimestamp(Instant.parse(event.getTimestamp().toString()))
                        .setPayload(mapping.toDeviceAddedEventAvro(event.getDeviceAdded())).build());

        eventProducer.send(record);
    }
}
