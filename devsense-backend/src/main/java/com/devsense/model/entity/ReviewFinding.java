package com.devsense.model.entity;

import com.devsense.model.enums.Severity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "findings")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReviewFinding {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(nullable = false, length = 20)
    private String agentType;   // SECURITY | PERFORMANCE | STYLE

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    private String category;    // 'SQL Injection', 'N+1 Query', etc.

    @Column(length = 500)
    private String filePath;

    private Integer lineStart;
    private Integer lineEnd;
    // Integer (capital I) allows null — int (lowercase) cannot be null

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String suggestion;

    @Column(columnDefinition = "TEXT")
    private String codeSnippet;

    @Column(columnDefinition = "TEXT")
    private String fixedSnippet;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
