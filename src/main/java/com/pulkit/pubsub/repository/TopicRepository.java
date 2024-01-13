package com.pulkit.pubsub.repository;

import com.pulkit.pubsub.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {

    Optional<Topic> findByName(String topicName);
}
