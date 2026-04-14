package com.devsense.exception;

public class GitHubApiException extends RuntimeException {
    // RuntimeException = unchecked exception — caller doesn't have to catch it

    public GitHubApiException(String message) {
        super(message);
    }

    public GitHubApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

