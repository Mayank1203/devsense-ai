package com.devsense.model.dto;

import com.devsense.model.enums.Severity;
import lombok.*;

@Data @Builder
public class FindingDto {
    private Long   id;
    private String agentType;   // SECURITY | PERFORMANCE | STYLE
    private Severity severity;  // CRITICAL | HIGH | MEDIUM | LOW | INFO
    private String category;
    private String filePath;
    private Integer lineStart;
    private Integer lineEnd;
    private String title;
    private String description;
    private String suggestion;
    private String codeSnippet;
    private String fixedSnippet;
}
