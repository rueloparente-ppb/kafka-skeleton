package com.rueloparente.kafka.wikimedia.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WikiConverter {
    private static final Logger log = LoggerFactory.getLogger(WikiConverter.class);
    private final ObjectMapper objectMapper;

    public WikiConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public com.rueloparente.kafkaskeleton.proto.RecentChange convert(String jsonEventData) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonEventData);

            // Build the MetaData part
            JsonNode metaNode = rootNode.path("meta");
            com.rueloparente.kafkaskeleton.proto.MetaData.Builder metaBuilder = com.rueloparente.kafkaskeleton.proto.MetaData.newBuilder()
                    .setUri(metaNode.path("uri").asText(""))
                    .setRequestId(metaNode.path("request_id").asText(""))
                    .setId(metaNode.path("id").asText("")) // meta.id
                    .setDomain(metaNode.path("domain").asText(""))
                    .setStream(metaNode.path("stream").asText(""))
                    .setTopic(metaNode.path("topic").asText(""))
                    .setPartition(metaNode.path("partition").asInt(0))
                    .setOffset(metaNode.path("offset").asLong(0L));


            // Build the main RecentChange object
            com.rueloparente.kafkaskeleton.proto.RecentChange.Builder builder = com.rueloparente.kafkaskeleton.proto.RecentChange.newBuilder()
                    .setMeta(metaBuilder.build()) // Set the built MetaData
                    .setId(rootNode.path("id").asLong(0L)) // top-level id
                    .setType(rootNode.path("type").asText(""))
                    .setNamespace(rootNode.path("namespace").asInt(0))
                    .setTitle(rootNode.path("title").asText(""))
                    .setTitleUrl(rootNode.path("title_url").asText(""))
                    .setComment(rootNode.path("comment").asText(""))
                    .setUser(rootNode.path("user").asText(""))
                    .setBot(rootNode.path("bot").asBoolean(false))
                    .setNotifyUrl(rootNode.path("notify_url").asText(""))
                    .setServerUrl(rootNode.path("server_url").asText(""))
                    .setServerName(rootNode.path("server_name").asText(""))
                    .setServerScriptPath(rootNode.path("server_script_path").asText(""))
                    .setWiki(rootNode.path("wiki").asText(""))
                    .setParsedcomment(rootNode.path("parsedcomment").asText(""));


            return builder.build();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON string into JsonNode: {}", jsonEventData, e);
            return null; // Or throw a custom exception
        } catch (Exception e) {
            log.error("Unexpected error during JSON to Proto conversion: {}", jsonEventData, e);
            return null; // Or throw
        }
    }
}
