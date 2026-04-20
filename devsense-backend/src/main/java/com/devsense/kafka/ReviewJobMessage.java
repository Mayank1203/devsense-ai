package com.devsense.kafka;

import lombok.*;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewJobMessage {

    private UUID   reviewId;
    // reviewId is used as the Kafka partition key
    // ensures all messages for the same review go to the same partition
    // (ordering guarantee for the same review)

    private String repoUrl;
    private String language;
    private List<String> focusAreas;
    private UUID   userId;
}

