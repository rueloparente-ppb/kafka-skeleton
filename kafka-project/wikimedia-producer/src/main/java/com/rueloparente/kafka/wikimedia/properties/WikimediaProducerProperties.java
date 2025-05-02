package com.rueloparente.kafka.wikimedia.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kafka")
public record WikimediaProducerProperties(String bootstrapServers, int port, String topic,
                                          String keySerializer, String valueSerializer,
                                          String partitionAssigmentStrategy,
                                          SaslProperties saslProperties) {
    public record SaslProperties(String securityProtocol, String saslMechanism, String saslJaasConfig,
                                 String sslEndpointIdentificationAlgorithm) {
    }
}
