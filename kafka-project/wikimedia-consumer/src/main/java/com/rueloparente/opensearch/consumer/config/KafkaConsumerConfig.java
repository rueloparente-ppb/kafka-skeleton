package com.rueloparente.opensearch.consumer.config;

import com.rueloparente.kafkaskeleton.proto.RecentChange;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import com.rueloparente.opensearch.consumer.properties.WikimediaConsumerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	private final WikimediaConsumerProperties properties;

	public KafkaConsumerConfig(WikimediaConsumerProperties properties) {
		this.properties = properties;
	}

	@Bean(name="recentChangeKafkaConsumer")
	public Properties recentChangeKafkaConsumer() {
		Properties props = new Properties();
		props.putAll(getConfig());
		return props;
	}

	@Bean
	public Map<String, Object> getConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put("bootstrap.servers", properties.bootStrapServer());
		props.put("group.id", properties.groupId());
		props.put("client.id", properties.clientId());
		props.put("enable.auto.commit", properties.enableAutoCommit());
		props.put("partition.assignment.strategy", properties.partitionAssigmentStrategy());
		props.put("key.deserializer", properties.keyDeserializer());
		props.put("value.deserializer", properties.valueDeserializer());
		props.put("auto.offset.reset", properties.autoOffsetReset());
		props.put("receive.buffer.bytes", properties.receiveBufferBytes());
		props.put("max.partition.fetch.bytes", properties.maxPartitionFetchBytes());

		// Add SASL properties if they are provided
		if (properties.saslProperties() != null) {
			WikimediaConsumerProperties.SaslProperties sasl = properties.saslProperties();
			props.put("security.protocol", sasl.securityProtocol());
			props.put("sasl.mechanism", sasl.saslMechanism());
			props.put("sasl.jaas.config", sasl.saslJaasConfig());
			props.put("ssl.endpoint.identification.algorithm", sasl.sslEndpointIdentificationAlgorithm());
		}

		return props;
	}
}
