package com.rueloparente.opensearch.consumer.infrastructure.kafka;

import com.rueloparente.kafkaskeleton.proto.RecentChange;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WikimediaEventProcessor implements EventProcessor<String, RecentChange> {

	private static final Logger logger = LoggerFactory.getLogger(WikimediaEventProcessor.class);

	@Override
	public void process(ConsumerRecord<String, RecentChange> record) {
		logger.info("Processing event: Key = {}, Value = {}, Partition = {}, Offset = {}",
				record.key(), record.value(), record.partition(), record.offset());

		// --- Your actual event processing logic for a String message ---
		String processedValue = record.value().toString().toUpperCase() + " [PROCESSED]";
		logger.info("Transformed value: {}", processedValue);
	}
}
