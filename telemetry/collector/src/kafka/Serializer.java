package kafka;

import org.apache.kafka.common.header.Headers;

import java.io.Closeable;
import java.util.Map;

public interface Serializer<T> extends Closeable {
    default void configure(Map<String, ?> configs, boolean isKey) {

    }

    byte[] serialize(String topic, T data);

    default byte[] serialize(String topic, Headers headers, T data) {
        return serialize(topic, data);
    }

    @Override
    default void close() {

    }
}
