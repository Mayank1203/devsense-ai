package com.devsense.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewProducer {

    private final KafkaTemplate<String, ReviewJobMessage> kafkaTemplate;
    private static final String TOPIC = "review-jobs";

    public void publishReviewJob(ReviewJobMessage message) {
        // reviewId.toString() is the Kafka KEY
        // Same key = same partition = ordering guarantee for the same review
        kafkaTemplate.send(TOPIC, message.getReviewId().toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish review job: {} — {}",
                                message.getReviewId(), ex.getMessage());
                    } else {
                        log.info("Published review job {} to partition {}",
                                message.getReviewId(),
                                result.getRecordMetadata().partition());
                    }
                });
    }
}
