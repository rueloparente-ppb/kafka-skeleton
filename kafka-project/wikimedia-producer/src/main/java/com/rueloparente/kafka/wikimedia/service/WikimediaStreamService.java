package com.rueloparente.kafka.wikimedia.service;

import com.launchdarkly.eventsource.EventSource;
import com.rueloparente.kafka.wikimedia.handler.WikimediaEventHandler;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class WikimediaStreamService {
private final WikimediaEventHandler wikimediaEventHandler;

public WikimediaStreamService(WikimediaEventHandler wikimediaEventHandler) {
    this.wikimediaEventHandler = wikimediaEventHandler;
}
@PostConstruct
public void startEventSource(){
    String url = "https://stream.wikimedia.org/v2/stream/recentchange";
    EventSource.Builder builder = new EventSource.Builder(wikimediaEventHandler, URI.create(url));
    EventSource eventSource = builder.build();


    eventSource.start();
}
}
