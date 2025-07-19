package com.compliance.dto;

public class SonarComponent {
    private String id;
    private String key;
    private String name;
    private String description;
    private String qualifier;
    private SonarMeasure[] measures;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getQualifier() { return qualifier; }
    public void setQualifier(String qualifier) { this.qualifier = qualifier; }

    public SonarMeasure[] getMeasures() { return measures; }
    public void setMeasures(SonarMeasure[] measures) { this.measures = measures; }
} 