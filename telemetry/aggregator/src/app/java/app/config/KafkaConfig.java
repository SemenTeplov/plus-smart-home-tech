package app.java.app.config;

import kafka.serializer.GeneralAvroSerializer;
import kafka.serializer.SensorEventDeserializer;
import kafka.serializer.SensorsSnapshotDeserializer;

import lombok.RequiredArgsConstructor;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    @Value("${kafka.bootstrap-servers}")
    private String server;

    @Bean
    public Consumer<String, SensorEventAvro> eventConsumer() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorEventDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregation-starter-group");

        return new KafkaConsumer<>(configs);
    }

    @Bean
    public Consumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SensorsSnapshotDeserializer.class);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "aggregation-starter-group");

        return new KafkaConsumer<>(configs);
    }

    @Bean
    public Producer<String, SensorsSnapshotAvro> snapshotProducer() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralAvroSerializer.class);

        return new KafkaProducer<>(configs);
    }
}
