package com.rueloparente.opensearch.consumer.infrastructure.kafka.processor;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public abstract class AbstractKafkaConsumerProcessor<K, V> implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKafkaConsumerProcessor.class);


    protected final KafkaConsumer<K, V> kafkaConsumer;

	protected AbstractKafkaConsumerProcessor(KafkaConsumer<K, V> kafkaConsumer) {
		this.kafkaConsumer = kafkaConsumer;
	}

	@Override
	public void run() {
		try {
			handleStartup();
			while (true) {
				ConsumerRecords<K, V> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
				if (!consumerRecords.isEmpty()) {
					processRecords(consumerRecords);
					processCommit();
				}
				checkHealth();
			}
		} catch (Exception exception) {
			handleShutdown(exception);
		}
	}

	protected abstract void processRecords(ConsumerRecords<K, V> consumerRecords);

	protected abstract void processCommit();

	protected void handleStartup() {
    }
	protected void handleShutdown(Exception exception) {
        String currentThreadName = Thread.currentThread().getName();
        LOGGER.error("operation=handleKafkaConsumerException, msg='Received an exception on thread {}.'",
                currentThreadName, exception);
        kafkaConsumer.close(Duration.ofMillis(150));
	}

	/**
	 * Responsible to check if the consumer is healthy to continue performing the pooling loop.
	 * Throws an Exception if the consumer should be shutdown.
	 */
	protected void checkHealth() throws Exception {
		// NoOp function
	}

}