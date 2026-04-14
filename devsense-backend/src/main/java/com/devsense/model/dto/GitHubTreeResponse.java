package com.devsense.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GitHubTreeResponse {

    @JsonProperty("sha")
    private String sha;

    @JsonProperty("url")
    private String url;

    @JsonProperty("tree")
    private List<TreeItem> tree;
    // This is the list of all files and folders in the repo

    @Data
    public static class TreeItem {
        private String path;   // e.g. 'src/main/java/UserService.java'
        private String type;   // 'blob' (file) or 'tree' (folder)
        private String sha;
        private Long size;     // file size in bytes
        private String url;
    }
}

