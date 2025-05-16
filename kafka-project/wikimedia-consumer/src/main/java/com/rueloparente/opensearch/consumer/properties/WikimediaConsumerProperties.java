package com.rueloparente.opensearch.consumer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record WikimediaConsumerProperties(String groupId, String bootStrapServer, String clientId, String partitionAssigmentStrategy,
                                          String keyDeserializer, String valueDeserializer, Integer receiveBufferBytes,
                                          Integer maxPartitionFetchBytes, String autoOffsetReset, Boolean enableAutoCommit, String topic,
                                          SaslProperties saslProperties ) {

	public record SaslProperties(String securityProtocol, String saslMechanism, String saslJaasConfig,
	                             String sslEndpointIdentificationAlgorithm) {
	}
}
