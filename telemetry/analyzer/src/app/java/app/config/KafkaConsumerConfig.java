package app.java.app.config;

import kafka.serializer.HubEventDeserializer;
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
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
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

    @Value("${kafka.group.hub-group}")
    private String hubGroup;

    @Value("${kafka.offset.rest-config}")
    private String offsetRest;

    @Value("${kafka.consumer.session-timeout-ms}")
    private String sessionTimeout;

    @Value("${kafka.consumer.heartbeat-interval-ms}")
    private String heartbeatInterval;

    @Value("${kafka.properties.reconnect-backoff-ms}")
    private String reconnectBackoff;

    @Value("${kafka.properties.reconnect-backoff-max-ms}")
    private String reconnectBackoffMax;

    @Value("${kafka.properties.retry-backoff-ms}")
    private String retryBackoff;

    @Value("${kafka.consumer.enable-auto-commit}")
    private Boolean autoCommit;

    @Bean
    public ConsumerFactory<String, HubEventAvro> hubConsumerFactory() {
        Map<String, Object> configs = consumerConfig();

        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, HubEventDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, hubGroup);

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
    public ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> hubConsumer(
            ConsumerFactory<String, HubEventAvro> eventConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, HubEventAvro> factory =
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
        configs.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, reconnectBackoff);
        configs.put(ConsumerConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, reconnectBackoffMax);
        configs.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackoff);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);

        return configs;
    }
}
