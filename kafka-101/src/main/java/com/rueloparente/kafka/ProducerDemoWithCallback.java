package com.rueloparente.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class);

    public static void main(String[] args) {
        log.info("Starting producer demo");

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        ProducerRecord<String, String> record = new ProducerRecord<>("demo_topic", "Hello World");

        sendMesageWithProducer(producer, record, 50);

        // tell the producer to send all data and block until done -- synchronous
        producer.flush();

        producer.close();
    }

    private static void sendMesageWithProducer(KafkaProducer<String, String> producer,
                                               ProducerRecord<String, String> record, int numMessages) {

        for (int i = 0; i < numMessages; i++) {
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //executes every time a record successfully sent or an exception is sent
                    if (e == null) {
                        log.info("Successfully sent message \n"
                                + "Topic: " + recordMetadata.topic()
                                + "\n Partition: " + recordMetadata.partition()
                                + "\n Offset: " + recordMetadata.offset()
                                + "\n Timestamp: " + recordMetadata.timestamp());
                    } else {
                        log.error("Error sending message", e);
                    }
                }
            });
        }

    }

}
