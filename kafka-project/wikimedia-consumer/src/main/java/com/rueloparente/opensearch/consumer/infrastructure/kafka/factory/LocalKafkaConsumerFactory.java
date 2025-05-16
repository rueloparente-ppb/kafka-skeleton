package com.rueloparente.opensearch.consumer.infrastructure.kafka.factory;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class LocalKafkaConsumerFactory<K, V> {

	public KafkaConsumer<K, V> getKafkaConsumer(Properties properties) {
		return new KafkaConsumer<>(properties);
	}
}
