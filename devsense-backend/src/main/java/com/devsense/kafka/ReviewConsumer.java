package com.devsense.kafka;

import com.devsense.model.entity.ReviewFinding;
import com.devsense.model.dto.AgentResult;
import com.devsense.model.dto.FindingDto;
import com.devsense.model.enums.ReviewStatus;
import com.devsense.model.enums.Severity;
import com.devsense.repository.FindingRepository;
import com.devsense.repository.ReviewRepository;
import com.devsense.service.AgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewConsumer {

    private final AgentService      agentService;
    private final ReviewRepository  reviewRepository;
    private final FindingRepository findingRepository;

    @KafkaListener(topics = "review-jobs", groupId = "devsense-consumers")
    @Transactional
    public void consume(ReviewJobMessage message) {
        UUID reviewId = message.getReviewId();
        log.info("Received review job: {}", reviewId);

        // Step 1: Mark as PROCESSING — client sees 'in progress'
        reviewRepository.updateStatusAndStartTime(
                reviewId, ReviewStatus.PROCESSING, LocalDateTime.now());

        try {
            // Step 2: Call Python agent service (blocking — may take 20-90 seconds)
            AgentResult result = agentService.runReview(message);

            // Step 3: Save findings to DB
            if (result.getFindings() != null && !result.getFindings().isEmpty()) {
                List<ReviewFinding> entities = result.getFindings().stream()
                        .map(f -> mapToEntity(f, reviewId))
                        .toList();
                findingRepository.saveAll(entities);
            }

            // Step 4: Mark COMPLETED
            reviewRepository.updateCompleted(
                    reviewId,
                    ReviewStatus.COMPLETED,
                    result.getOverallScore() != null ? result.getOverallScore() : BigDecimal.ZERO,
                    result.getSummary(),
                    LocalDateTime.now());

            log.info("Review {} completed with score {}", reviewId, result.getOverallScore());

        } catch (Exception e) {
            // Step 5: Mark FAILED — never let consumer crash (would block the partition)
            log.error("Review {} failed: {}", reviewId, e.getMessage(), e);
            reviewRepository.updateFailed(
                    reviewId, ReviewStatus.FAILED, e.getMessage());
        }
    }

    private ReviewFinding mapToEntity(FindingDto dto, UUID reviewId) {
        // We need the Review entity reference — use a proxy
        com.devsense.model.entity.Review reviewRef = new com.devsense.model.entity.Review();
        reviewRef.setId(reviewId);
        return ReviewFinding.builder()
                .review(reviewRef)
                .agentType(dto.getAgentType())
                .severity(dto.getSeverity() != null ? dto.getSeverity() : Severity.INFO)
                .category(dto.getCategory())
                .filePath(dto.getFilePath())
                .lineStart(dto.getLineStart())
                .lineEnd(dto.getLineEnd())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .suggestion(dto.getSuggestion())
                .codeSnippet(dto.getCodeSnippet())
                .fixedSnippet(dto.getFixedSnippet())
                .build();
    }
}
