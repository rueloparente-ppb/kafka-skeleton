package com.rueloparente.kafka.wikimedia.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import com.rueloparente.kafka.wikimedia.converters.WikiConverter;
import com.rueloparente.kafka.wikimedia.properties.WikimediaProducerProperties;
import com.rueloparente.kafkaskeleton.proto.RecentChange;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WikimediaEventHandler implements EventHandler {

    private KafkaTemplate<String, RecentChange> kafkaTemplate;
    private WikiConverter wikiConverter;
    private WikimediaProducerProperties kafkaProperties;

    public WikimediaEventHandler(KafkaTemplate<String, RecentChange> kafkaTemplate,
                                 WikimediaProducerProperties kafkaProperties, WikiConverter wikiConverter) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaProperties = kafkaProperties;
        this.wikiConverter = wikiConverter;
    }

    @Override
    public void onOpen() {

    }

    @Override
    public void onClosed() {

    }

    @Override
    public void onMessage(String s, MessageEvent messageEvent) {
        RecentChange recentChange = wikiConverter.convert(messageEvent.getData());

        //We could pass a key here
        kafkaTemplate.send(kafkaProperties.topic(), recentChange);

    }

    @Override
    public void onComment(String s) {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}
