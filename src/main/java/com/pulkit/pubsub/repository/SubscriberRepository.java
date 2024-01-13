package com.pulkit.pubsub.repository;

import com.pulkit.pubsub.entities.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, String> {
}
