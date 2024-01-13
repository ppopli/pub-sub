package com.pulkit.pubsub.service.impl;

import com.pulkit.pubsub.entities.Message;
import com.pulkit.pubsub.entities.MessageOutbox;
import com.pulkit.pubsub.entities.Subscriber;
import com.pulkit.pubsub.entities.Topic;
import com.pulkit.pubsub.model.Constants;
import com.pulkit.pubsub.model.PublishMessageDTO;
import com.pulkit.pubsub.model.SubscribeToTopicDTO;
import com.pulkit.pubsub.model.UnsubscribeTopicDTO;
import com.pulkit.pubsub.repository.MessageOutboxRepository;
import com.pulkit.pubsub.repository.MessageRepository;
import com.pulkit.pubsub.repository.SubscriberRepository;
import com.pulkit.pubsub.repository.TopicRepository;
import com.pulkit.pubsub.service.PubSubService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubSubServiceImpl implements PubSubService {

    private final TopicRepository topicRepository;
    private final MessageRepository messageRepository;
    private final SubscriberRepository subscriberRepository;
    private final MessageOutboxRepository messageOutboxRepository;
    @Override
    public void createTopic(String topicName) {
        Topic topic = Topic.builder()
                .name(topicName)
                .build();

        topicRepository.save(topic);
    }

    @Override
    @Transactional
    public void publishMessage(String topicName, PublishMessageDTO messageReq) {
        Topic topic = topicRepository.findByName(topicName)
                .orElseThrow(() -> new RuntimeException("Topic doesn't exists " + topicName));

        Set<Subscriber> subscribers = topic.getSubscribers();

        Message message = Message
                .builder()
                .content(messageReq.getMessage())
                .topic(topic)
                .build();
        final Message savedMessage = messageRepository.save(message);
        final Set<MessageOutbox> messages = new HashSet<>();
        subscribers.forEach((s) -> {
            MessageOutbox messageOutbox = MessageOutbox
                    .builder()
                    .topicId(topic.getId())
                    .status(Constants.MessageStatus.PENDING)
                    .messageId(savedMessage.getId())
                    .subscriberId(s.getSubscriberId())
                    .build();
            messages.add(messageOutbox);
        });
        messageOutboxRepository.saveAll(messages);
    }

    @Override
    @Transactional
    public void subscribe(String topicName, SubscribeToTopicDTO subscriptionReq) {
        Topic topic = topicRepository.findByName(topicName)
                .orElseThrow(() -> new RuntimeException("Topic doesn't exists " + topicName));
        Subscriber subscriber = subscriberRepository.findById(subscriptionReq.getSubscriber())
                .orElseGet(() -> {
                    Subscriber s =Subscriber
                            .builder()
                            .subscriberId(subscriptionReq.getSubscriber())
                            .webhookEndpoint(subscriptionReq.getWebhookEndpoint())
                            .build();
                    return subscriberRepository.save(s);
                });
        topic.getSubscribers().add(subscriber);
        topicRepository.save(topic);
    }

    @Override
    @Transactional
    public void unsubscribe(String topicName, UnsubscribeTopicDTO unsubscribeReq) {
        Topic topic = topicRepository.findByName(topicName)
                .orElseThrow(() -> new RuntimeException("Topic doesn't exists " + topicName));
        Subscriber s = subscriberRepository
                .findById(unsubscribeReq.getSubscriber())
                .orElseThrow(() -> new RuntimeException("Subscriber doesn't exists " + unsubscribeReq.getSubscriber()));
        topic.getSubscribers().remove(s);
        topicRepository.save(topic);
    }
}
