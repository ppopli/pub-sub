package com.pulkit.pubsub.controller;

import com.pulkit.pubsub.model.PublishMessageDTO;
import com.pulkit.pubsub.model.SubscribeToTopicDTO;
import com.pulkit.pubsub.model.UnsubscribeTopicDTO;
import com.pulkit.pubsub.service.PubSubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/{topicName}")
@RequiredArgsConstructor
public class PubSubController {

    private final PubSubService pubSubService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTopic(@PathVariable String topicName) {
        log.info("[topic = {}] request received to create topic", topicName);
        pubSubService.createTopic(topicName);
    }

    @PostMapping(value = "/publish")
    @ResponseStatus(HttpStatus.OK)
    public void publish(@PathVariable String topicName, @RequestBody PublishMessageDTO message) {
        log.info("[topic = {}][message = {}] request received to publish message to topic", topicName, message);
        pubSubService.publishMessage(topicName, message);
    }

    @PostMapping(value = "/subscribe")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void subscribe(@PathVariable String topicName, @RequestBody SubscribeToTopicDTO subscriptionRequest) {
        log.info("[topic = {}][subscription request = {}] request received to subscribe to topic",
                topicName, subscriptionRequest);
        pubSubService.subscribe(topicName, subscriptionRequest);
    }

    @PostMapping(value = "/unsubscribe")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void unsubscribe(@PathVariable String topicName, @RequestBody UnsubscribeTopicDTO unsubscribeRequest) {
        log.info("[topic = {}][unsubscribe req = {}] request received to unsubscribe to topic",
                topicName, unsubscribeRequest);
        pubSubService.unsubscribe(topicName, unsubscribeRequest);
    }

}
