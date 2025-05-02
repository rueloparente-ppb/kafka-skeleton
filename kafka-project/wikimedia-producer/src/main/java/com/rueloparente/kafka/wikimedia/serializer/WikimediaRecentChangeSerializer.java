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

    /**
     * Serializes the WikimediaRecentChange message into a byte array.
     * Prepends the message with its length (Varint).
     *
     * @param topic The topic the record is being sent to (ignored)
     * @param data  The WikimediaRecentChange message object to serialize. Can be null.
     * @return Byte array representation of the message, or null if data is null.
     */
    @Override
    public byte[] serialize(String topic, RecentChange data) {
        if (data == null) {
            log.trace("Serializing null data to null byte array");
            return null;
        }

        // Using ByteArrayOutputStream is convenient but potentially less performant
        // than pre-calculating size for high-throughput scenarios.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // writeDelimitedTo writes the size of the message first (as a Varint)
            // followed by the message itself. This is useful for consumers
            // that need to know message boundaries.
            data.writeDelimitedTo(outputStream);
            byte[] bytes = outputStream.toByteArray();
            log.trace("Serialized WikimediaRecentChange to {} bytes", bytes.length);
            return bytes;
        } catch (Exception e) {
            // Consider a more specific exception handling strategy if needed
            log.error("Error serializing WikimediaRecentChange data for topic {}: {}", topic, data.toString(), e);
            throw new RuntimeException("Error serializing Protobuf message", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
        log.debug("Closing WikimediaRecentChangeSerializer");
    }
}