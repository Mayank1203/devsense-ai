package com.devsense.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class ReviewRequestDto {

    @NotBlank(message = "Repository URL is required")
    @Pattern(
            regexp = "^https://github\\.com/[\\w.-]+/[\\w.-]+$",
            message = "Must be a valid GitHub URL: https://github.com/owner/repo"
    )
    private String repoUrl;

    @NotBlank(message = "Language is required")
    private String language;   // java, python, javascript, typescript, etc.

    private List<String> focusAreas;
    // Optional — if null defaults to [security, performance, style] in service
}

