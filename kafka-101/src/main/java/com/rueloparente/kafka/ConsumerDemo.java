package com.rueloparente.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemo {
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemo.class);

    public static void main(String[] args) {
    log.info("Starting consumer demo");
    Properties props = new Properties();
    props.put("bootstrap.servers", "localhost:9092");

    props.put("key.deserializer", StringDeserializer.class.getName());
    props.put("value.deserializer", StringDeserializer.class.getName());
    props.put("group.id", "demo_group");
    props.put("auto.offset.reset", "earliest");

    KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

    consumer.subscribe(Arrays.asList("demo_topic"));

    while (true) {
        log.info("Polling for records...");
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

        for (ConsumerRecord<String, String> consumerRecord : records) {
            log.info("Key: " + consumerRecord.key() + ", Value: " + consumerRecord.value());
        }

    }

    }
}
