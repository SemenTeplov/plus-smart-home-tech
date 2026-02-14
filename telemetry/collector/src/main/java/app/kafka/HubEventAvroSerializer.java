package app.kafka;

import app.constants.Exceptions;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.errors.SerializationException;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class HubEventAvroSerializer implements Serializer<HubEventAvro> {
    private final EncoderFactory encoderFactory = EncoderFactory.get();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, HubEventAvro event) {
        if (event == null) {
            return null;
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            BinaryEncoder encoder = encoderFactory.binaryEncoder(outputStream, null);
            DatumWriter<HubEventAvro> datumWriter = new SpecificDatumWriter<>(HubEventAvro.class);

            datumWriter.write(event, encoder);
            encoder.flush();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new SerializationException(Exceptions.EXCEPTION_SERIALIZATION);
        }
    }

    @Override
    public void close() {

    }
}
