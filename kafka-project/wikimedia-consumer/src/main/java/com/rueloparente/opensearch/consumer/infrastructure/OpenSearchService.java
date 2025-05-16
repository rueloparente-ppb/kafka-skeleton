package com.rueloparente.opensearch.consumer.infrastructure;

import com.rueloparente.opensearch.consumer.properties.OpenSearchProperties;
import jakarta.annotation.PostConstruct;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenSearchService {
	private static final Logger logger = LoggerFactory.getLogger(OpenSearchService.class);

	private final OpenSearchProperties openSearchProperties;
	private final OpenSearchClient openSearchClient;
	private final String wikimediaIndexName;

	public OpenSearchService(OpenSearchProperties openSearchProperties, OpenSearchClient openSearchClient) {
		this.openSearchProperties = openSearchProperties;
		this.openSearchClient = openSearchClient;
		this.wikimediaIndexName = openSearchProperties.index().wikimedia();
	}

	@PostConstruct
	public void ensureIndexExists() throws IOException {
		ExistsRequest existsRequest = new ExistsRequest.Builder()
				.index(wikimediaIndexName)
				.build();
		boolean indexExists = openSearchClient.indices().exists(existsRequest).value();


		if (!indexExists) {
			CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(wikimediaIndexName).build();
			CreateIndexResponse createIndexResponse = openSearchClient.indices().create(createIndexRequest);
			if (createIndexResponse.acknowledged()) {
				logger.info("The Wikimedia Index '{}' has been created!", wikimediaIndexName);
			}
		} else {
			logger.info("The Wikimedia Index '{}' already exists.", wikimediaIndexName);
		}
	}

}