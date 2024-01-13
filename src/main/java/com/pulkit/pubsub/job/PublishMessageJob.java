package com.pulkit.pubsub.job;

import com.pulkit.pubsub.client.HttpClient;
import com.pulkit.pubsub.entities.Message;
import com.pulkit.pubsub.entities.MessageOutbox;
import com.pulkit.pubsub.entities.Subscriber;
import com.pulkit.pubsub.entities.Topic;
import com.pulkit.pubsub.model.Constants;
import com.pulkit.pubsub.model.PublishMessageDTO;
import com.pulkit.pubsub.repository.MessageOutboxRepository;
import com.pulkit.pubsub.repository.MessageRepository;
import com.pulkit.pubsub.repository.SubscriberRepository;
import com.pulkit.pubsub.repository.TopicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.transaction.jta.platform.internal.SynchronizationRegistryBasedSynchronizationStrategy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PublishMessageJob {

    private final MessageOutboxRepository messageOutboxRepository;
    private final MessageRepository messageRepository;
    private final SubscriberRepository subscriberRepository;
    private final TopicRepository topicRepository;
    private final HttpClient client;
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            20,
            100,
            10,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(1000)
    );

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    @Transactional
    public void pushMessageToSubscriber() {
        log.info("Running message publishing job");
        List<MessageOutbox> messageOutboxes = messageOutboxRepository
                .updateStatusToInProgress(20)
                .orElseGet(Collections::emptyList);

        messageOutboxes.parallelStream().forEach(messageOutbox -> {
            CompletableFuture.runAsync(() -> {
                Topic topic = getTopic(messageOutbox.getTopicId());
                Subscriber subscriber = getSubscriber(messageOutbox.getSubscriberId(), topic);

                Message message = messageRepository
                        .findById(messageOutbox.getMessageId())
                        .orElseThrow(() -> new RuntimeException("No message found"));

                client.sendMessage(subscriber.getWebhookEndpoint(),
                        PublishMessageDTO
                                .builder()
                                .message(message.getContent())
                                .build());

            }).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("[message outbox = {}][subscriber = {}] message delivery failed to subscriber.",
                            exception.getMessage(), messageOutbox.getSubscriberId(), exception);
                    messageOutbox.setStatus(Constants.MessageStatus.PENDING_RETRY);
                    messageOutbox.setRetryCount(messageOutbox.getRetryCount() + 1);
                    messageOutboxRepository.save(messageOutbox);
                }  else {
                    log.info("[message outbox = {}] [subscriber = {}] message delivered to subscriber successfully.",
                            messageOutbox, messageOutbox.getSubscriberId());
                    messageOutbox.setStatus(Constants.MessageStatus.SENT);
                    messageOutboxRepository.save(messageOutbox);
                }
            });
        });
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 120000)
    @Transactional
    public void retryFailedMessages() {
        log.info("Running message publishing retry job");
        List<MessageOutbox> failedMessages = messageOutboxRepository
                .findTop10ByStatusOrderByCreatedAtAsc(Constants.MessageStatus.PENDING_RETRY)
                .orElseGet(Collections::emptyList);
        failedMessages.parallelStream().forEach(messageOutbox -> {
            CompletableFuture.runAsync(() -> {
                if (messageOutbox.getRetryCount() < 3) {
                    Topic topic = getTopic(messageOutbox.getTopicId());
                    Subscriber subscriber = getSubscriber(messageOutbox.getSubscriberId(), topic);
                    Message message = messageRepository
                            .findById(messageOutbox.getMessageId())
                            .orElseThrow(() -> new RuntimeException("No message found"));

                    client.sendMessage(subscriber.getWebhookEndpoint(),
                            PublishMessageDTO
                                    .builder()
                                    .message(message.getContent())
                                    .build());
                }
            }).whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("[message outbox = {}] [subscriber = {}] message delivered to subscriber successfully.",
                            messageOutbox, messageOutbox.getSubscriberId());
                    messageOutbox.setStatus(Constants.MessageStatus.SENT);
                    messageOutboxRepository.save(messageOutbox);
                } else {
                    messageOutbox.setRetryCount(messageOutbox.getRetryCount() + 1);
                    log.error("[message outbox = {}][subscriber = {}] message delivery failed to subscriber.",
                            exception.getMessage(), messageOutbox.getSubscriberId(), exception);
                    if (messageOutbox.getRetryCount() > 2) {
                        messageOutbox.setStatus(Constants.MessageStatus.FAILED);
                    }
                    messageOutboxRepository.save(messageOutbox);
                }
            });
        });

    }

    private Topic getTopic(UUID topicId) {
        return topicRepository
                .findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found " + topicId));
    }

    private Subscriber getSubscriber(String subscriberId, Topic topic) {
        return topic.getSubscribers()
                .stream()
                .filter(s -> Objects.equals(s.getSubscriberId(), subscriberId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No Subscriber found"));
    }

}
