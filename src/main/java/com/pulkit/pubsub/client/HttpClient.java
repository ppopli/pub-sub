package com.pulkit.pubsub.client;

import com.pulkit.pubsub.model.PublishMessageDTO;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpClient {
    private final RestTemplate restTemplate;

    public HttpClient(RestTemplateBuilder restTemplateBuilder) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public void sendMessage(String uri, PublishMessageDTO messageDTO) {
        restTemplate.postForEntity(uri, messageDTO, Void.class);
    }
}
