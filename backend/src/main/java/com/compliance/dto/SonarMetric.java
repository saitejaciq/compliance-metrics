package com.compliance.dto;

public class SonarMetric {
    private String id;
    private String key;
    private String type;
    private String name;
    private String description;
    private String domain;
    private int direction;
    private boolean qualitative;
    private boolean hidden;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public int getDirection() { return direction; }
    public void setDirection(int direction) { this.direction = direction; }

    public boolean isQualitative() { return qualitative; }
    public void setQualitative(boolean qualitative) { this.qualitative = qualitative; }

    public boolean isHidden() { return hidden; }
    public void setHidden(boolean hidden) { this.hidden = hidden; }
} 