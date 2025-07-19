package com.compliance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SnykProjectsResponse {
    private SnykJsonApi jsonapi;
    private List<SnykProject> data;
    private SnykLinks links;

    // Getters and Setters
    public SnykJsonApi getJsonapi() { return jsonapi; }
    public void setJsonapi(SnykJsonApi jsonapi) { this.jsonapi = jsonapi; }

    public List<SnykProject> getData() { return data; }
    public void setData(List<SnykProject> data) { this.data = data; }

    public SnykLinks getLinks() { return links; }
    public void setLinks(SnykLinks links) { this.links = links; }

    public static class SnykJsonApi {
        private String version;

        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    public static class SnykLinks {
        // Empty for now as the response doesn't show pagination links
    }
} 