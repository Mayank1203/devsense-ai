package com.devsense.model.dto;

import lombok.Data;

@Data
public class GitHubContentResponse {
    private String name;
    private String path;
    private String content;
    // GitHub returns file content as BASE64 encoded string
    // We decode it to get the actual source code
    private String encoding;  // always 'base64'
    private Long size;
}

