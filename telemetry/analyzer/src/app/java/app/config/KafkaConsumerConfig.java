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

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> snapshotConsumer(
            ConsumerFactory<String, SensorsSnapshotAvro> snapshotConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, SensorsSnapshotAvro> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(snapshotConsumerFactory);

        return factory;
    }

    private Map<String, Object> consumerConfig() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetRest);
        configs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeout);
        configs.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatInterval);

        return configs;
    }
}
