package com.devsense.service;

import com.devsense.model.entity.Review;
import com.devsense.model.entity.User;
import com.devsense.exception.ResourceNotFoundException;
import com.devsense.kafka.ReviewJobMessage;
import com.devsense.kafka.ReviewProducer;
import com.devsense.model.dto.*;
import com.devsense.model.enums.ReviewStatus;
import com.devsense.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository  reviewRepository;
    private final FindingRepository findingRepository;
    private final UserRepository    userRepository;
    private final ReviewProducer    reviewProducer;

    @Transactional
    public ReviewResponseDto initiateReview(ReviewRequestDto req) {
        User user = currentUser();

        // Check monthly limit (plan-based)
        if (user.getReviewsUsed() >= user.getReviewsLimit()) {
            throw new IllegalStateException(
                    "Monthly review limit reached (" + user.getReviewsLimit() + "). Upgrade your plan.");
        }

        // Save review with PENDING status
        Review review = Review.builder()
                .user(user)
                .repoUrl(req.getRepoUrl())
                .language(req.getLanguage())
                .focusAreas(req.getFocusAreas() != null
                        ? req.getFocusAreas()
                        : List.of("security", "performance", "style"))
                .status(ReviewStatus.PENDING)
                .build();
        review = reviewRepository.save(review);

        // Increment usage counter atomically
        user.setReviewsUsed(user.getReviewsUsed() + 1);
        userRepository.save(user);

        // Publish to Kafka — non-blocking, agent picks it up asynchronously
        ReviewJobMessage msg = ReviewJobMessage.builder()
                .reviewId(review.getId())
                .repoUrl(req.getRepoUrl())
                .language(req.getLanguage())
                .focusAreas(review.getFocusAreas())
                .userId(user.getId())
                .build();
        reviewProducer.publishReviewJob(msg);

        log.info("Review {} created and queued for user {}", review.getId(), user.getEmail());
        return mapToDto(review, List.of());
    }

    public ReviewResponseDto getReview(UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Review review = reviewRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Review", id.toString()));
        List<FindingDto> findings = findingRepository.findByReviewId(id)
                .stream().map(this::findingToDto).toList();
        return mapToDto(review, findings);
    }

    public Page<ReviewResponseDto> listReviews(Pageable pageable) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return reviewRepository.findByUserEmail(email, pageable)
                .map(r -> mapToDto(r, List.of()));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    public ReviewResponseDto mapToDto(Review r, List<FindingDto> findings) {
        return ReviewResponseDto.builder()
                .id(r.getId()).repoUrl(r.getRepoUrl()).language(r.getLanguage())
                .status(r.getStatus()).overallScore(r.getOverallScore())
                .summary(r.getSummary()).errorMessage(r.getErrorMessage())
                .findings(findings)
                .createdAt(r.getCreatedAt()).completedAt(r.getCompletedAt())
                .build();
    }

    private FindingDto findingToDto(com.devsense.entity.ReviewFinding f) {
        return FindingDto.builder()
                .id(f.getId()).agentType(f.getAgentType()).severity(f.getSeverity())
                .category(f.getCategory()).filePath(f.getFilePath())
                .lineStart(f.getLineStart()).lineEnd(f.getLineEnd())
                .title(f.getTitle()).description(f.getDescription())
                .suggestion(f.getSuggestion()).codeSnippet(f.getCodeSnippet())
                .fixedSnippet(f.getFixedSnippet())
                .build();
    }
}
