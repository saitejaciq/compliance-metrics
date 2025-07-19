package com.compliance.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "snyk")
public class SnykConfig {
    private String baseUrl;
    private String token;
    private String organization;
    private String apiVersion;
    private Map<String, Product> products;

    // Getters and Setters
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }

    public Map<String, Product> getProducts() { return products; }
    public void setProducts(Map<String, Product> products) { this.products = products; }

    public static class Product {
        private String name;
        private String tag;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
    }
} 