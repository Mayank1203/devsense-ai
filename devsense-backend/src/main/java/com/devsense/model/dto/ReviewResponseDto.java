package com.devsense.model.dto;

import com.devsense.model.enums.ReviewStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Data @Builder
public class ReviewResponseDto {

    private UUID          id;
    private String        repoUrl;
    private String        language;
    private ReviewStatus  status;    // PENDING | PROCESSING | COMPLETED | FAILED
    private BigDecimal    overallScore;  // null until COMPLETED
    private String        summary;
    private String        errorMessage;  // null unless FAILED
    private List<FindingDto> findings;   // null until COMPLETED
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
