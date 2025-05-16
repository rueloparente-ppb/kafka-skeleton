package com.rueloparente.opensearch.consumer.infrastructure.kafka.processor;

import com.rueloparente.opensearch.consumer.infrastructure.kafka.EventProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class KafkaConsumerProcessor<K, V> extends AbstractKafkaConsumerProcessor<K, V> {

	private final EventProcessor<K, V> eventProcessor;
	private final int commitIntervalMs;
	private long lastCommitTimeMs;

	public KafkaConsumerProcessor(KafkaConsumer<K, V> kafkaConsumer, EventProcessor<K, V> eventProcessor,
	                              int commitIntervalMs) {
		super(kafkaConsumer);


		this.eventProcessor = eventProcessor;
		this.commitIntervalMs = commitIntervalMs;
		this.lastCommitTimeMs = System.currentTimeMillis();
	}

	@Override
	protected void processRecords(ConsumerRecords<K, V> consumerRecords) {
		consumerRecords.forEach(eventProcessor::process);
	}

	@Override
	protected void processCommit() {
		if (System.currentTimeMillis() - lastCommitTimeMs >= commitIntervalMs) {
			kafkaConsumer.commitAsync();
			lastCommitTimeMs = System.currentTimeMillis();
		}
	}
}
