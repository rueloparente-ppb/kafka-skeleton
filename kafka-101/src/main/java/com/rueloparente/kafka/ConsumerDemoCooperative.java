package com.rueloparente.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoCooperative {
    private static final Logger log = LoggerFactory.getLogger(ConsumerDemoCooperative.class);

    public static void main(String[] args) {
        log.info("Starting consumer demo");
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");

        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("group.id", "demo_group2");
        props.put("auto.offset.reset", "earliest");
        props.put("partition.assignment.strategy", CooperativeStickyAssignor.class.getName());

        //props.put("group.instance.id", "demo_group2"); // this will assign a custom id to the consumer, allowing for static assignments

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        final Thread mainThread = Thread.currentThread();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Shutting down consumer demo");
                consumer.wakeup();
                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        consumer.subscribe(Arrays.asList("demo_topic"));

        startConsumerWithShutdown(consumer);
    }

    private static void startConsumerWithShutdown(KafkaConsumer<String, String> consumer) {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    log.info("Key: " + record.key() + ", Value: " + record.value());
                }

            }
        } catch (WakeupException e) {
            log.info("Consumer is starting shutdown");
        } catch (Exception e) {
            log.error("Error while consuming records", e);
        } finally {
            consumer.close();
            log.info("The consumer has been shut down gracefully");
        }
    }
}
