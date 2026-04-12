package com.devsense.model.enums;

public enum ReviewStatus {
    PENDING,      // job received, not started yet
    PROCESSING,   // Kafka consumer picked it up, agent is running
    COMPLETED,    // all agents finished, findings saved
    FAILED        // something went wrong — error_message has details
}
