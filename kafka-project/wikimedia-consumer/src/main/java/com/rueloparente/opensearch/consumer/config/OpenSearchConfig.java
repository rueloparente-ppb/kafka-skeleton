package com.rueloparente.opensearch.consumer.config;

import com.rueloparente.opensearch.consumer.properties.OpenSearchProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenSearchConfig {
	@Bean
	public OpenSearchClient openSearchClient(final OpenSearchProperties openSearchProperties) {

		final HttpHost host = new HttpHost(openSearchProperties.host(), openSearchProperties.port(),
				openSearchProperties.httpsEnabled() ? "https" : "http");

		RestClient restClient;
		if (openSearchProperties.httpsEnabled()) {
			BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(openSearchProperties.username(),
					openSearchProperties.password()));
			restClient = RestClient.builder(host).
					setHttpClientConfigCallback(httpClientBuilder ->
							httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();
		} else {
			restClient = RestClient.builder(host).build();
		}

		final OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		return new OpenSearchClient(transport);
	}
}
