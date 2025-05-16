package com.rueloparente.kafka.wikimedia.serializer; // Adjust package name as needed

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rueloparente.kafkaskeleton.proto.RecentChange;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class WikimediaRecentChangeSerializer implements Serializer<RecentChange> {

    private static final Logger log = LoggerFactory.getLogger(WikimediaRecentChangeSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

        log.debug("Configuring WikimediaRecentChangeSerializer. Is key serializer: {}", isKey);
    }

    @Override
    public byte[] serialize(String topic, RecentChange data) {
        if (data == null) {
            log.trace("Serializing null data to null byte array");
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {

            data.writeDelimitedTo(outputStream);
            byte[] bytes = outputStream.toByteArray();
            log.trace("Serialized WikimediaRecentChange to {} bytes", bytes.length);
            return bytes;
        } catch (Exception e) {
            log.error("Error serializing WikimediaRecentChange data for topic {}: {}", topic, data.toString(), e);
            throw new RuntimeException("Error serializing Protobuf message", e);
        }
    }

    @Override
    public void close() {
        log.debug("Closing WikimediaRecentChangeSerializer");
    }
}