package com.devsense.model.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AgentResult {
    private List<FindingDto> findings;
    private BigDecimal       overallScore;
    private String           summary;
}

