package com.devsense.service;

import com.devsense.exception.GitHubApiException;
import com.devsense.model.dto.GitHubContentResponse;
import com.devsense.model.dto.GitHubTreeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.Base64;

@Service
@Slf4j
public class GitHubService {

    private final WebClient githubWebClient;

    // @Qualifier needed because we have two WebClient beans
    // Spring uses the bean name 'githubWebClient' to find the right one
    public GitHubService(@Qualifier("githubWebClient") WebClient githubWebClient) {
        this.githubWebClient = githubWebClient;
    }

    // Main method — called by AgentService
    // Returns list of maps, each map has: {path, content, language}
    public List<Map<String, String>> fetchRepoFiles(String repoUrl, String language) {
        // Parse owner and repo name from URL
        // Input:  https://github.com/spring-projects/spring-petclinic
        // Output: owner='spring-projects', repo='spring-petclinic'
        String cleaned = repoUrl.replace("https://github.com/", "").replace(".git", "");
        String[] parts = cleaned.split("/");
        if (parts.length < 2) {
            throw new GitHubApiException("Invalid GitHub URL: " + repoUrl);
        }
        String owner = parts[0];
        String repo  = parts[1];

        log.info("Fetching file tree for {}/{}", owner, repo);

        // Step 1: Get the full file tree
        GitHubTreeResponse tree = githubWebClient.get()
                .uri("/repos/{owner}/{repo}/git/trees/HEAD?recursive=1", owner, repo)
                // ?recursive=1 returns ALL files in ALL subdirectories
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        response -> Mono.error(new GitHubApiException(
                                "GitHub repo not found or private: " + repoUrl)))
                .bodyToMono(GitHubTreeResponse.class)
                .block();  // block() converts reactive to synchronous

        if (tree == null || tree.getTree() == null) {
            return List.of();
        }

        // Step 2: Determine file extension to filter by
        String ext = resolveExtension(language);

        // Step 3: Collect reviewable files (up to 30)
        List<String> filesToFetch = tree.getTree().stream()
                .filter(item -> "blob".equals(item.getType()))      // files only, not folders
                .filter(item -> item.getPath().endsWith(ext))         // correct language
                .filter(item -> !item.getPath().contains("/test/"))  // skip test files
                .filter(item -> item.getSize() != null
                        && item.getSize() < 50000)               // skip files > 50KB
                .map(GitHubTreeResponse.TreeItem::getPath)
                .limit(30)                                             // max 30 files
                .toList();

        log.info("Found {} reviewable files for {}/{}", filesToFetch.size(), owner, repo);

        // Step 4: Fetch content of each file
        List<Map<String, String>> result = new ArrayList<>();
        for (String path : filesToFetch) {
            Map<String, String> fileData = fetchFileContent(owner, repo, path, language);
            if (fileData != null) {
                result.add(fileData);
            }
        }
        return result;
    }

    private Map<String, String> fetchFileContent(
            String owner, String repo, String path, String language) {
        try {
            GitHubContentResponse content = githubWebClient.get()
                    .uri("/repos/{owner}/{repo}/contents/{path}", owner, repo, path)
                    .retrieve()
                    .bodyToMono(GitHubContentResponse.class)
                    .block();

            if (content == null || content.getContent() == null) return null;

            // GitHub returns content as base64 — decode it to get actual source code
            String decoded = new String(
                    Base64.getMimeDecoder().decode(content.getContent()));
            //  getMimeDecoder() handles GitHub's multi-line base64 (has newlines)

            return Map.of(
                    "path",     path,
                    "content",  decoded,
                    "language", language
            );
        } catch (Exception e) {
            log.warn("Could not fetch file {}: {}", path, e.getMessage());
            return null;  // skip this file, continue with others
        }
    }

    private String resolveExtension(String language) {
        if (language == null) return ".java";  // default
        return switch (language.toLowerCase()) {
            case "java"       -> ".java";
            case "python"     -> ".py";
            case "javascript" -> ".js";
            case "typescript" -> ".ts";
            case "kotlin"     -> ".kt";
            case "go"         -> ".go";
            default            -> "." + language.toLowerCase();
        };
    }
}
