package com.pulkit.pubsub.repository;

import com.pulkit.pubsub.entities.MessageOutbox;
import com.pulkit.pubsub.model.Constants;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageOutboxRepository extends JpaRepository<MessageOutbox, UUID> {

    @Modifying
    @Query(value = "Update message_outbox set status = 'IN_PROGRESS' " +
            "where id in (SELECT id FROM message_outbox WHERE status = 'PENDING' " +
            "ORDER BY created_at ASC LIMIT :limit) RETURNING *", nativeQuery = true)
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    Optional<List<MessageOutbox>> updateStatusToInProgress(@Param("limit") int limit);

    Optional<List<MessageOutbox>> findTop10ByStatusOrderByCreatedAtAsc(Constants.MessageStatus status);
}
