package com.devsense.controller;

import com.devsense.model.dto.*;
import com.devsense.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 202 Accepted — job is queued, not finished yet
    // Client must poll GET /reviews/{id} until status == COMPLETED
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> create(
            @Valid @RequestBody ReviewRequestDto req) {
        ReviewResponseDto response = reviewService.initiateReview(req);
        return ResponseEntity.accepted().body(response);
    }

    // Client polls this endpoint until status != PENDING/PROCESSING
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getReview(id));
    }

    // List all reviews for the currently authenticated user
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ReviewResponseDto>> list(
            @PageableDefault(size=10, sort="createdAt",
                    direction=Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.listReviews(pageable));
    }
}
// commit this 