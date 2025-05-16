package com.rueloparente.opensearch.consumer.infrastructure.kafka.manager;

import com.rueloparente.opensearch.consumer.infrastructure.kafka.factory.LocalKafkaConsumerFactory;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;

import java.util.Properties;

public abstract class AbstractConsumerManager<K, V> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConsumerManager.class);
	private final Properties consumerManagerProperties;
	protected LocalKafkaConsumerFactory<K, V> localKafkaConsumerFactory;
	protected int commitIntervalMs;
	protected Thread kafkaConsumerThread;
	protected volatile boolean consumerActive = false;
	protected KafkaConsumer<K, V> kafkaConsumer;
	private Thread shutdownHook;

	protected AbstractConsumerManager(LocalKafkaConsumerFactory<K, V> localKafkaConsumerFactory,
	                                  Properties consumerManagerProperties) {

		this.localKafkaConsumerFactory = localKafkaConsumerFactory;
		this.consumerManagerProperties = consumerManagerProperties;
		this.shutdownHook = new Thread(() -> {
			LOGGER.info("Shutdown hook triggered for AbstractConsumerManager. Attempting to stop consumer...");
			boolean isActive = this.consumerActive;
			KafkaConsumer<K, V> consumerInstance = this.kafkaConsumer;
			Thread consumerThreadInstance = this.kafkaConsumerThread;

			if (isActive && consumerInstance != null) {
				LOGGER.info("Consumer is marked active. Waking up Kafka consumer instance via shutdown hook.");
				try {
					consumerInstance.wakeup();

					if (consumerThreadInstance != null && consumerThreadInstance.isAlive()) {
						LOGGER.info("Shutdown hook: Waiting for Kafka consumer thread to join (max 5000ms)...");
						consumerThreadInstance.join(5000);
						if (consumerThreadInstance.isAlive()) {
							LOGGER.warn("Shutdown hook: Kafka consumer thread [{}] did not shut down gracefully in time.", consumerThreadInstance.getName());
						} else {
							LOGGER.info("Shutdown hook: Kafka consumer thread [{}] shut down successfully.", consumerThreadInstance.getName());
						}
					}
				} catch (InterruptedException e) {
					LOGGER.warn("Shutdown hook: Interrupted while waiting for consumer thread [{}] to join.",
							(consumerThreadInstance != null ? consumerThreadInstance.getName() : "unknown"), e);
					Thread.currentThread().interrupt();
				} catch (Exception e) {
					LOGGER.error("Shutdown hook: Error during Kafka consumer wakeup/join sequence.", e);
				} finally {
					this.consumerActive = false;
					LOGGER.info("Shutdown hook: AbstractConsumerManager marked consumer as inactive.");
				}
			} else {
				LOGGER.info("Shutdown hook: Consumer was not active or kafkaConsumer instance was null. No direct action taken by hook.");
			}
		});
		Runtime.getRuntime().addShutdownHook(this.shutdownHook);
	}

	@PostConstruct
	protected void start() {
		if (!consumerActive) {
			LOGGER.info("Starting consumer...");


			kafkaConsumer = localKafkaConsumerFactory.getKafkaConsumer(consumerManagerProperties);
			startConsumer();
			if (kafkaConsumerThread != null) {
				kafkaConsumerThread.start();
				consumerActive = true;
				LOGGER.info("Consumer started successfully. Thread: {}", kafkaConsumerThread.getName());
			} else {
				LOGGER.error("startConsumer() did not initialize kafkaConsumerThread. Consumer not started.");
				if (kafkaConsumer != null) {
					try {
						kafkaConsumer.close();
					} catch (Exception e) {
						LOGGER.error("Error closing KafkaConsumer after failed thread initialization.", e);
					}
					kafkaConsumer = null;
				}
			}
		} else if (consumerActive) {
			LOGGER.warn("Attempted to start consumer, but it is already active.");
		}
	}

	protected abstract void startConsumer();

	public boolean stopConsumer() {
		LOGGER.info("stopConsumer operation called.");
		if (!consumerActive) {
			LOGGER.warn("operation=stopConsumer, msg='Consumer is not active or already stopped!'");
			return kafkaConsumer == null && kafkaConsumerThread == null;
		}
		stopConsumers();
		consumerActive = false;
		LOGGER.info("Consumer stop requested and processed. Marked as inactive.");
		return true;
	}

	protected void stopConsumers() {
		LOGGER.info("stopConsumers internal method called.");
		KafkaConsumer<K, V> consumerToWakeUp = this.kafkaConsumer;
		Thread threadToJoin = this.kafkaConsumerThread;

		try {
			if (consumerToWakeUp != null) {
				LOGGER.debug("Waking up Kafka consumer instance: {}", consumerToWakeUp);
				consumerToWakeUp.wakeup();
			} else {
				LOGGER.debug("KafkaConsumer instance is null, cannot wakeup.");
			}

			if (threadToJoin != null && threadToJoin.isAlive()) {
				LOGGER.debug("Waiting for consumer thread [{}] to join (max 2000ms)...", threadToJoin.getName());
				try {
					threadToJoin.join(2000);
					if (threadToJoin.isAlive()) {
						LOGGER.warn("Consumer thread [{}] did not join in the allocated time during stopConsumers.", threadToJoin.getName());
					} else {
						LOGGER.info("Consumer thread [{}] joined successfully during stopConsumers.", threadToJoin.getName());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					LOGGER.warn("Interrupted while waiting for consumer thread [{}] to join in stopConsumers.", threadToJoin.getName(), e);
				}
			}

		} catch (Exception exception) {
			LOGGER.error("operation=stopConsumers, msg='Error during consumer stop sequence.'", exception);
		} finally {
			LOGGER.debug("Nullifying kafkaConsumerThread and kafkaConsumer references in stopConsumers.");
			this.kafkaConsumerThread = null;
			this.kafkaConsumer = null;
		}
	}

	 @ManagedOperation(description = "Stops the currently running consumer (if it exists) and " +
	"starts a single Kafka consumer with the received commit interval")
	public boolean safeStartConsumer() {
		if (consumerActive) {
			LOGGER.info("Consumer is active, attempting to stop it before restarting...");
			if (!stopConsumer()) {
				LOGGER.warn("safeStartConsumer: Failed to stop the currently active consumer cleanly, but proceeding to start a new one if toggle allows.");

			}
		}
		start();
		return consumerActive;
	}
}
