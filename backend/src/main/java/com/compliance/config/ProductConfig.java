package com.compliance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "sonarcloud")
public class ProductConfig {
    
    private String baseUrl;
    private String token;
    private String organization;
    private Map<String, Product> products;
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getOrganization() {
        return organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    public Map<String, Product> getProducts() {
        return products;
    }
    
    public void setProducts(Map<String, Product> products) {
        this.products = products;
    }
    
    public static class Product {
        private String name;
        private String[] projects;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String[] getProjects() {
            return projects;
        }
        
        public void setProjects(String[] projects) {
            this.projects = projects;
        }
    }
} 