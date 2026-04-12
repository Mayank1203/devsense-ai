package com.devsense.model.entity;

import com.devsense.model.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "reviews")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Review {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // JoinColumn(name='user_id') = this column in reviews table is the FK
    // FetchType.LAZY = don't load User unless you call review.getUser()
    private User user;

    @Column(nullable = false, length = 500)
    private String repoUrl;

    private String repoOwner;
    private String repoName;
    private String language;

    @ElementCollection
    @CollectionTable(
            name = "review_focus_areas",
            joinColumns = @JoinColumn(name = "review_id")
    )
    @Column(name = "focus_area")
    // @ElementCollection stores List<String> in a separate table automatically
    // review_focus_areas table: review_id | focus_area
    //                          uuid1      | security
    //                          uuid1      | performance
    @Builder.Default
    private List<String> focusAreas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    private BigDecimal overallScore;  // null until COMPLETED
    private String summary;
    private String errorMessage;

    @Builder.Default private int totalFiles    = 0;
    @Builder.Default private int filesReviewed = 0;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReviewFinding> findings = new ArrayList<>();

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
