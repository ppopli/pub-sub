package com.pulkit.pubsub.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@SuperBuilder
public class Subscriber extends BaseEntity {
    @Id
    private String subscriberId;

    @Column(name= "webhook_endpoint", nullable = false, unique = true)
    private String webhookEndpoint;
}
