package com.rueloparente.opensearch.consumer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "persistence.opensearch")
public record OpenSearchProperties(
		String host,
		int port,
		boolean httpsEnabled,
		boolean authEnabled,
		String username,
		String password,
		Index index
) {
	public record SearchSize(int wikimedia) {
	}

	public record Index(String wikimedia) {
	}
}
