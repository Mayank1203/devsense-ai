package com.devsense.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

    private String accessToken;
    // Short-lived JWT (24 hours) — client sends this on every request
    // Authorization: Bearer <accessToken>

    private String tokenType;
    // Always 'Bearer' — part of OAuth2 standard

    private String email ;
    private String fullName;
    private String plan ;
    // Plan returned so frontend can show correct UI (limited vs unlimited)

}
