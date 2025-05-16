package com.rueloparente.opensearch.consumer.infrastructure.kafka.manager;

import com.rueloparente.kafkaskeleton.proto.RecentChange;
import com.rueloparente.opensearch.consumer.infrastructure.kafka.EventProcessor;
import com.rueloparente.opensearch.consumer.infrastructure.kafka.factory.LocalKafkaConsumerFactory;
import com.rueloparente.opensearch.consumer.infrastructure.kafka.processor.KafkaConsumerProcessor;
import com.rueloparente.opensearch.consumer.properties.WikimediaConsumerProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;

@Component
public class WikimediaConsumerManager extends AbstractConsumerManager<String, RecentChange> {
	private final String kafkaTopic;
	private final EventProcessor<String, RecentChange> eventProcessor;

	public WikimediaConsumerManager(LocalKafkaConsumerFactory<String, RecentChange> localKafkaConsumerFactory,
	                                EventProcessor<String, RecentChange> eventProcessor, @Qualifier("recentChangeKafkaConsumer") Properties props,
	                                WikimediaConsumerProperties config) {
		super(localKafkaConsumerFactory, props);
		this.kafkaTopic = config.topic();
		this.eventProcessor = eventProcessor;
	}

	@Override
	protected void startConsumer() {
		kafkaConsumer.subscribe(Collections.singleton(kafkaTopic));
		kafkaConsumerThread = new Thread(
				new KafkaConsumerProcessor<>(kafkaConsumer, eventProcessor, commitIntervalMs)
		);
	}

}
