package app.handler.HubEventImpl;

import app.constants.Messages;
import app.handler.HubEventHandler;
import app.mapping.MappingHubEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import telemetry.messages.HubEventProto;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final MappingHubEvent mapping;

    private final Producer<String, SpecificRecordBase> eventProducer;

    @Value("${collector.kafka.topics.hub}")
    private String topic;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEventProto event) {
        log.info(Messages.MESSAGE_SEND_HUB, event);

        eventProducer.send(getRecord(event, mapping.toDeviceRemovedEventAvro(event.getDeviceRemoved()), topic));
    }
}
