package app.service.impl;

import app.constants.Messages;

import app.constants.Values;
import app.dto.HubEvent;
import app.dto.SensorEvent;
import app.mapper.HubEventToAvroMapper;

import app.mapper.SensorEventToAvroMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import app.service.EventService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Value("${kafka.topics.sensor}")
    private String sensorEventTopic;

    @Value("${kafka.topics.hub}")
    private String hubEventTopic;

    private final Producer<String, SensorEventAvro> sensorEventProducer;

    private final Producer<String, HubEventAvro> hubEventProducer;

    private final HubEventToAvroMapper hubEventToAvroMapper;

    private final SensorEventToAvroMapper sensorEventToAvroMapper;

    @Override
    public void collectSensorEvent(SensorEvent event) {
        log.info(Messages.MESSAGE_SEND_SENSOR, event);

        ProducerRecord<String, SensorEventAvro> record = new ProducerRecord<>(
                sensorEventTopic,
                sensorEventToAvroMapper.map(event));
        sendRecord(sensorEventProducer, record, Values.EVENT_TYPE_SENSOR);
    }

    @Override
    public void collectHubEvent(HubEvent event) {
        log.info(Messages.MESSAGE_SEND_HUB, event);

        ProducerRecord<String, HubEventAvro> record = new ProducerRecord<>(
                hubEventTopic,
                hubEventToAvroMapper.map(event));
        sendRecord(hubEventProducer, record, Values.EVENT_TYPE_HUB);
    }

    private <T> void sendRecord(Producer<String, T> producer, ProducerRecord<String, T> record, String eventType) {
        producer.send(record, (meta, exception) -> {
            if (exception == null) {
                log.info(Messages.SENT_MESSAGE_SUCCESSFUL, eventType, meta.topic(), meta.partition(), meta.offset());
            } else {
                log.error(Messages.SENT_MESSAGE_FAILED, eventType, record.value(), exception);
            }
        });
    }
}
