package app.java.app.config;

import kafka.serializer.SensorEventDeserializer;
import kafka.serializer.SensorsSnapshotDeserializer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.listener.ContainerProperties;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${aggregator.kafka.bootstrap-servers}")
    private String server;

    @Value("${aggregator.kafka.group.snapshot-group}")
    private String snapshotGroup;

    @Value("${aggregator.kafka.group.sensor-group}")
    private String sensorGroup;

    @Value("${aggregator.kafka.offset.rest-config}")
    private String offsetRest;

    @Value("${aggregator.kafka.consumer.session-timeout-ms}")
    private String sessionTimeout;

    @Value("${aggregator.kafka.consumer.heartbeat-interval-ms}")
    private String heartbeatInterval;

    @Value("${aggregator.kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Bean
    public ConsumerFactory<String, SensorEventAvro> eventConsumerFactory() {
        Map<String, Object> configs = consumerConfig();

        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, sensorGroup);

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory() {
        Map<String, Object> configs = consumerConfig();

        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotGroup);

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SensorEventAvro> eventConsumer(
            ConsumerFactory<String, SensorEventAvro> eventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SensorEventAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(eventConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> snapshotConsumer(
            ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snapshotConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    private Map<String, Object> consumerConfig() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        configs.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);

        return configs;
    }
}
