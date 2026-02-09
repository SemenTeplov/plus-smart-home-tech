package service.impl;

import constants.Messages;

import dto.HubEvent;
import dto.SensorEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import service.EventService;

import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Value("${kafka.topics.sensor}")
    private final String sensorEventTopic;

    @Value("${kafka.topics.hub}")
    private final String hubEventTopic;

    private final Properties sensorEventAvroSerializerConfig;

    private final Properties hubEventAvroSerializerConfig;

    @Override
    public void collectSensorEvent(SensorEvent event) {
        log.info(Messages.MESSAGE_SEND_SENSOR, event);

        ProducerRecord<String, SensorEventAvro> record =
                new ProducerRecord<>(sensorEventTopic, new SensorEventAvro(
                        event.getId(),
                        event.getHubId(),
                        event.getTimestamp(),
                        event));

        try (Producer<String, SensorEventAvro> producer = new KafkaProducer<>(sensorEventAvroSerializerConfig)) {
            producer.send(record);
        }
    }

    @Override
    public void collectHubEvent(HubEvent event) {
        log.info(Messages.MESSAGE_SEND_HUB, event);

        ProducerRecord<String, HubEventAvro> record =
                new ProducerRecord<>(hubEventTopic, new HubEventAvro(
                        event.getHubId(),
                        event.getTimestamp(),
                        event));

        try (Producer<String, HubEventAvro> producer = new KafkaProducer<>(hubEventAvroSerializerConfig)) {
            producer.send(record);
        }
    }
}
