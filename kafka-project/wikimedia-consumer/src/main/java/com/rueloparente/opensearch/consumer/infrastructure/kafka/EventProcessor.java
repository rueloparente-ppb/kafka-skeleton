package com.rueloparente.opensearch.consumer.infrastructure.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

@FunctionalInterface
public interface EventProcessor <K, V>{
	void process(ConsumerRecord<K, V> record);
}
