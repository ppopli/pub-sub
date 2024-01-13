package com.pulkit.pubsub.model;

public class Constants {

    public enum MessageStatus {
        PENDING,
        IN_PROGRESS,
        PENDING_RETRY,
        SENT,
        FAILED,
    }
}
