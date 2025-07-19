package com.compliance.dto;

import java.util.List;

public class ProductDto {
    private String key;
    private String name;
    private List<SonarProject> projects;
    
    public ProductDto() {}
    
    public ProductDto(String key, String name, List<SonarProject> projects) {
        this.key = key;
        this.name = name;
        this.projects = projects;
    }
    
    // Getters and Setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<SonarProject> getProjects() { return projects; }
    public void setProjects(List<SonarProject> projects) { this.projects = projects; }
} 