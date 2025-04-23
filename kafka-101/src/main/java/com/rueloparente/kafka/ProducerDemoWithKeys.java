package com.rueloparente.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKeys {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithKeys.class);

    public static void main(String[] args) throws InterruptedException {
        log.info("Starting producer demo");

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String message = "Hello World";

        sendMesageWithProducer(producer, message, 10);
        Thread.sleep(500);
        sendMesageWithProducer(producer, message, 10);
        Thread.sleep(500);
        sendMesageWithProducer(producer, message, 10);

        // tell the producer to send all data and block until done -- synchronous
        producer.flush();

        producer.close();
    }

    private static void sendMesageWithProducer(KafkaProducer<String, String> producer,
                                               String message, int numMessages) {

        for (int i = 0; i < numMessages; i++) {
            String key = "id_" + i;
            String value = message + " " + i;

            ProducerRecord<String, String> record = new ProducerRecord<>("demo_topic", key, value);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //executes every time a record successfully sent or an exception is sent
                    if (e == null) {
                        log.info("Key: {} | Partition: {}", key, recordMetadata.partition());
                    } else {
                        log.error("Error sending message", e);
                    }
                }
            });
        }

    }

}
