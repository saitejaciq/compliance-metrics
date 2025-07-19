package com.compliance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SnykProject {
    private String type;
    private String id;
    private SnykAttributes attributes;
    private SnykRelationships relationships;

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public SnykAttributes getAttributes() { return attributes; }
    public void setAttributes(SnykAttributes attributes) { this.attributes = attributes; }

    public SnykRelationships getRelationships() { return relationships; }
    public void setRelationships(SnykRelationships relationships) { this.relationships = relationships; }

    public static class SnykAttributes {
        private String name;
        private String type;
        @JsonProperty("target_file")
        private String targetFile;
        @JsonProperty("target_reference")
        private String targetReference;
        private String origin;
        private String created;
        private String status;
        @JsonProperty("business_criticality")
        private List<String> businessCriticality;
        private List<String> environment;
        private List<String> lifecycle;
        private List<SnykTag> tags;
        @JsonProperty("read_only")
        private boolean readOnly;
        private SnykSettings settings;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getTargetFile() { return targetFile; }
        public void setTargetFile(String targetFile) { this.targetFile = targetFile; }

        public String getTargetReference() { return targetReference; }
        public void setTargetReference(String targetReference) { this.targetReference = targetReference; }

        public String getOrigin() { return origin; }
        public void setOrigin(String origin) { this.origin = origin; }

        public String getCreated() { return created; }
        public void setCreated(String created) { this.created = created; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public List<String> getBusinessCriticality() { return businessCriticality; }
        public void setBusinessCriticality(List<String> businessCriticality) { this.businessCriticality = businessCriticality; }

        public List<String> getEnvironment() { return environment; }
        public void setEnvironment(List<String> environment) { this.environment = environment; }

        public List<String> getLifecycle() { return lifecycle; }
        public void setLifecycle(List<String> lifecycle) { this.lifecycle = lifecycle; }

        public List<SnykTag> getTags() { return tags; }
        public void setTags(List<SnykTag> tags) { this.tags = tags; }

        public boolean isReadOnly() { return readOnly; }
        public void setReadOnly(boolean readOnly) { this.readOnly = readOnly; }

        public SnykSettings getSettings() { return settings; }
        public void setSettings(SnykSettings settings) { this.settings = settings; }
    }

    public static class SnykTag {
        private String key;
        private String value;

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public static class SnykSettings {
        @JsonProperty("recurring_tests")
        private SnykRecurringTests recurringTests;
        @JsonProperty("pull_requests")
        private Object pullRequests;

        public SnykRecurringTests getRecurringTests() { return recurringTests; }
        public void setRecurringTests(SnykRecurringTests recurringTests) { this.recurringTests = recurringTests; }

        public Object getPullRequests() { return pullRequests; }
        public void setPullRequests(Object pullRequests) { this.pullRequests = pullRequests; }
    }

    public static class SnykRecurringTests {
        private String frequency;

        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
    }

    public static class SnykRelationships {
        private SnykRelationship organization;
        private SnykRelationship target;
        private SnykRelationship importer;

        public SnykRelationship getOrganization() { return organization; }
        public void setOrganization(SnykRelationship organization) { this.organization = organization; }

        public SnykRelationship getTarget() { return target; }
        public void setTarget(SnykRelationship target) { this.target = target; }

        public SnykRelationship getImporter() { return importer; }
        public void setImporter(SnykRelationship importer) { this.importer = importer; }
    }

    public static class SnykRelationship {
        private SnykRelationshipData data;
        private SnykRelationshipLinks links;

        public SnykRelationshipData getData() { return data; }
        public void setData(SnykRelationshipData data) { this.data = data; }

        public SnykRelationshipLinks getLinks() { return links; }
        public void setLinks(SnykRelationshipLinks links) { this.links = links; }
    }

    public static class SnykRelationshipData {
        private String type;
        private String id;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    public static class SnykRelationshipLinks {
        private String related;

        public String getRelated() { return related; }
        public void setRelated(String related) { this.related = related; }
    }
} 