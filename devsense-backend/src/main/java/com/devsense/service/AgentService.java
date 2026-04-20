package com.devsense.service;

import com.devsense.kafka.ReviewJobMessage;
import com.devsense.model.dto.AgentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Service
@Slf4j
public class AgentService {

    private final WebClient agentWebClient;

    @Value("${agent.timeout-seconds:120}")
    private int timeoutSeconds;

    public AgentService(@Qualifier("agentWebClient") WebClient agentWebClient) {
        this.agentWebClient = agentWebClient;
    }

    public AgentResult runReview(ReviewJobMessage msg) {
        // TODO: implement in Week 4 when Python agent service is ready
        // For now return an empty result so the pipeline can be tested end-to-end
        log.info("[STUB] AgentService.runReview() called for review: {}", msg.getReviewId());
        return AgentResult.builder()
                .findings(java.util.List.of())
                .overallScore(java.math.BigDecimal.valueOf(10.0))
                .summary("[STUB] Review completed — agent not yet implemented")
                .build();
    }
}
