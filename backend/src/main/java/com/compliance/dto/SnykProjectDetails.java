package com.compliance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class SnykProjectDetails {
    private String name;
    private String id;
    private String created;
    private String origin;
    private String type;
    private boolean readOnly;
    private String testFrequency;
    private int totalDependencies;
    private SnykIssueCounts issueCountsBySeverity;
    private String remoteRepoUrl;
    private String imageId;
    private String imageTag;
    private String imagePlatform;
    private String hostname;
    private String lastTestedDate;
    private String browseUrl;
    private String owner;
    private SnykImportingUser importingUser;
    private boolean isMonitored;
    private List<SnykProject.SnykTag> tags;
    private SnykAttributes attributes;
    private SnykRemediation remediation;
    private String branch;
    private String targetReference;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCreated() { return created; }
    public void setCreated(String created) { this.created = created; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isReadOnly() { return readOnly; }
    public void setReadOnly(boolean readOnly) { this.readOnly = readOnly; }

    public String getTestFrequency() { return testFrequency; }
    public void setTestFrequency(String testFrequency) { this.testFrequency = testFrequency; }

    public int getTotalDependencies() { return totalDependencies; }
    public void setTotalDependencies(int totalDependencies) { this.totalDependencies = totalDependencies; }

    public SnykIssueCounts getIssueCountsBySeverity() { return issueCountsBySeverity; }
    public void setIssueCountsBySeverity(SnykIssueCounts issueCountsBySeverity) { this.issueCountsBySeverity = issueCountsBySeverity; }

    public String getRemoteRepoUrl() { return remoteRepoUrl; }
    public void setRemoteRepoUrl(String remoteRepoUrl) { this.remoteRepoUrl = remoteRepoUrl; }

    public String getImageId() { return imageId; }
    public void setImageId(String imageId) { this.imageId = imageId; }

    public String getImageTag() { return imageTag; }
    public void setImageTag(String imageTag) { this.imageTag = imageTag; }

    public String getImagePlatform() { return imagePlatform; }
    public void setImagePlatform(String imagePlatform) { this.imagePlatform = imagePlatform; }

    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }

    public String getLastTestedDate() { return lastTestedDate; }
    public void setLastTestedDate(String lastTestedDate) { this.lastTestedDate = lastTestedDate; }

    public String getBrowseUrl() { return browseUrl; }
    public void setBrowseUrl(String browseUrl) { this.browseUrl = browseUrl; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public SnykImportingUser getImportingUser() { return importingUser; }
    public void setImportingUser(SnykImportingUser importingUser) { this.importingUser = importingUser; }

    public boolean isMonitored() { return isMonitored; }
    public void setMonitored(boolean monitored) { isMonitored = monitored; }

    public List<SnykProject.SnykTag> getTags() { return tags; }
    public void setTags(List<SnykProject.SnykTag> tags) { this.tags = tags; }

    public SnykAttributes getAttributes() { return attributes; }
    public void setAttributes(SnykAttributes attributes) { this.attributes = attributes; }

    public SnykRemediation getRemediation() { return remediation; }
    public void setRemediation(SnykRemediation remediation) { this.remediation = remediation; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getTargetReference() { return targetReference; }
    public void setTargetReference(String targetReference) { this.targetReference = targetReference; }

    public static class SnykIssueCounts {
        private int low;
        private int high;
        private int medium;
        private int critical;

        public int getLow() { return low; }
        public void setLow(int low) { this.low = low; }

        public int getHigh() { return high; }
        public void setHigh(int high) { this.high = high; }

        public int getMedium() { return medium; }
        public void setMedium(int medium) { this.medium = medium; }

        public int getCritical() { return critical; }
        public void setCritical(int critical) { this.critical = critical; }
    }

    public static class SnykImportingUser {
        private String id;
        private String name;
        private String username;
        private String email;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class SnykAttributes {
        private List<String> criticality;
        private List<String> lifecycle;
        private List<String> environment;

        public List<String> getCriticality() { return criticality; }
        public void setCriticality(List<String> criticality) { this.criticality = criticality; }

        public List<String> getLifecycle() { return lifecycle; }
        public void setLifecycle(List<String> lifecycle) { this.lifecycle = lifecycle; }

        public List<String> getEnvironment() { return environment; }
        public void setEnvironment(List<String> environment) { this.environment = environment; }
    }

    public static class SnykRemediation {
        private Map<String, Object> pin;
        private Map<String, Object> patch;
        private Map<String, SnykUpgradeInfo> upgrade;

        public Map<String, Object> getPin() { return pin; }
        public void setPin(Map<String, Object> pin) { this.pin = pin; }

        public Map<String, Object> getPatch() { return patch; }
        public void setPatch(Map<String, Object> patch) { this.patch = patch; }

        public Map<String, SnykUpgradeInfo> getUpgrade() { return upgrade; }
        public void setUpgrade(Map<String, SnykUpgradeInfo> upgrade) { this.upgrade = upgrade; }
    }

    public static class SnykUpgradeInfo {
        private List<String> vulns;
        private List<String> upgrades;
        @JsonProperty("upgradeTo")
        private String upgradeTo;

        public List<String> getVulns() { return vulns; }
        public void setVulns(List<String> vulns) { this.vulns = vulns; }

        public List<String> getUpgrades() { return upgrades; }
        public void setUpgrades(List<String> upgrades) { this.upgrades = upgrades; }

        public String getUpgradeTo() { return upgradeTo; }
        public void setUpgradeTo(String upgradeTo) { this.upgradeTo = upgradeTo; }
    }
} 