package app.java.app.config;

import kafka.serializer.SensorEventDeserializer;
import kafka.serializer.SensorsSnapshotDeserializer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${kafka.bootstrap-servers}")
    private String server;

    @Value("${kafka.group.snapshot-group}")
    private String snapshotGroup;

    @Value("${kafka.group.sensor-group}")
    private String sensorGroup;

    @Value("${kafka.offset.rest-config}")
    private String offsetRest;

    @Bean
    public ConsumerFactory<String, SensorEventAvro> eventConsumer() {
        Map<String, Object> configs = consumerConfig();

        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, sensorGroup);

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumer() {
        Map<String, Object> configs = consumerConfig();

        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroup);

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    private Map<String, Object> consumerConfig() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetRest);

        return configs;
    }
}
