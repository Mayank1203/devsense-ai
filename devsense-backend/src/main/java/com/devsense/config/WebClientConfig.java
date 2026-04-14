package com.devsense.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // ── GitHub API WebClient ──────────────────────────────────────────────────
    @Bean(name = "githubWebClient")
    public WebClient githubWebClient(
            @Value("${github.token}") String token) {

        return WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader("Authorization", "Bearer " + token)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .codecs(c -> c.defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))  // 10MB buffer
                // Needed for large repos — default 256KB is too small
                .build();
    }

    // ── Python Agent Service WebClient ────────────────────────────────────────
    @Bean(name = "agentWebClient")
    public WebClient agentWebClient(
            @Value("${agent.base-url}") String baseUrl) {

        return WebClient.builder()
                .baseUrl(baseUrl)   // http://localhost:8001 (or http://agent:8001 in Docker)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
