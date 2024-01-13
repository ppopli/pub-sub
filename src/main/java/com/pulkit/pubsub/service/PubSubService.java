package com.pulkit.pubsub.service;

import com.pulkit.pubsub.model.PublishMessageDTO;
import com.pulkit.pubsub.model.SubscribeToTopicDTO;
import com.pulkit.pubsub.model.UnsubscribeTopicDTO;

public interface PubSubService {
    void createTopic(String topicName);
    void publishMessage(String topicName, PublishMessageDTO message);

    void subscribe(String topicName, SubscribeToTopicDTO subscriptionReq);
    void unsubscribe(String topicName, UnsubscribeTopicDTO unsubscribeReq);
}
