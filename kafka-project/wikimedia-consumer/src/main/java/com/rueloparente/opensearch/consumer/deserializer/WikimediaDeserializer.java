package com.rueloparente.opensearch.consumer.deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import com.rueloparente.kafkaskeleton.proto.RecentChange;

import java.io.ByteArrayInputStream;

public class WikimediaDeserializer implements Deserializer<RecentChange> {
	@Override
	public RecentChange deserialize(String topic, byte[] data) {
		if (data == null) {
			return null;
		}
		try {
			return RecentChange.parseDelimitedFrom(new ByteArrayInputStream(data));
		} catch (Exception e) {
			return RecentChange.newBuilder().build();
		}
	}
}
