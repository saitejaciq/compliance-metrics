package com.compliance.service;

import com.compliance.config.ProductConfig;
import com.compliance.dto.SonarMetric;
import com.compliance.dto.SonarProject;
import com.compliance.dto.SonarProjectsResponse;
import com.compliance.dto.SonarMeasuresResponse;
import com.compliance.dto.SonarMetricsResponse;
import com.compliance.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class SonarCloudService {
    private static final Logger logger = LoggerFactory.getLogger(SonarCloudService.class);

    @Autowired
    private ProductConfig productConfig;

    @Autowired
    private WebClient webClient;

    public List<SonarMetric> getAllMetrics() {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(productConfig.getBaseUrl())
                    .path("/metrics/search")
                    .queryParam("p", 1)
                    .queryParam("ps", 500)
                    .build()
                    .toUriString();

            // Print curl command for debugging
            String curlCommand = String.format("curl -X GET '%s' -H 'Authorization: Bearer %s'", 
                url, productConfig.getToken());
            logger.info("SonarCloud metrics API curl command: {}", curlCommand);

            SonarMetricsResponse response = webClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + productConfig.getToken())
                    .retrieve()
                    .bodyToMono(SonarMetricsResponse.class)
                    .block();

            if (response != null && response.getMetrics() != null) {
                logger.info("Retrieved {} SonarCloud metrics", response.getMetrics().size());
                return response.getMetrics();
            }
        } catch (Exception e) {
            logger.error("Error fetching SonarCloud metrics: {}", e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    public List<SonarProject> getAllProjects() {
        List<SonarProject> allProjects = new ArrayList<>();
        int page = 1;
        boolean hasMore = true;

        while (hasMore) {
            try {
                String url = UriComponentsBuilder
                        .fromHttpUrl(productConfig.getBaseUrl())
                        .path("/components/search")
                        .queryParam("organization", productConfig.getOrganization())
                        .queryParam("qualifiers", "TRK")
                        .queryParam("p", page)
                        .queryParam("ps", 500)
                        .build()
                        .toUriString();

                // Print curl command for debugging
                String curlCommand = String.format("curl -X GET '%s' -H 'Authorization: Bearer %s'", 
                    url, productConfig.getToken());
                logger.info("SonarCloud API curl command (page {}): {}", page, curlCommand);

                SonarProjectsResponse response = webClient.get()
                        .uri(url)
                        .header("Authorization", "Bearer " + productConfig.getToken())
                        .retrieve()
                        .bodyToMono(SonarProjectsResponse.class)
                        .block();

                if (response != null && response.getComponents() != null) {
                    allProjects.addAll(Arrays.asList(response.getComponents()));
                    logger.info("Retrieved {} projects from page {}", response.getComponents().length, page);
                    
                    // Check if there are more pages
                    if (response.getPaging() != null) {
                        int totalPages = (int) Math.ceil((double) response.getPaging().getTotal() / response.getPaging().getPageSize());
                        hasMore = page < totalPages && response.getComponents().length > 0;
                    } else {
                        hasMore = response.getComponents().length > 0;
                    }
                    page++;
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                logger.error("Error fetching SonarCloud projects from page {}: {}", page, e.getMessage(), e);
                hasMore = false;
            }
        }

        logger.info("Retrieved {} total SonarCloud projects", allProjects.size());
        return allProjects;
    }

    public List<SonarProject> getProjectsByProduct(String productKey) {
        ProductConfig.Product product = productConfig.getProducts().get(productKey);
        if (product == null) {
            logger.error("Product not found: {}", productKey);
            return new ArrayList<>();
        }

        List<SonarProject> allProjects = getAllProjects();
        List<String> productProjectKeys = Arrays.asList(product.getProjects());
        
        return allProjects.stream()
                .filter(project -> productProjectKeys.contains(project.getKey()))
                .collect(Collectors.toList());
    }

    public Map<String, String> getProjectMeasures(String projectKey, List<String> metricKeys) {
        Map<String, String> measures = new HashMap<>();
        
        try {
            // Encode metric keys for URL
            String encodedMetrics = metricKeys.stream()
                    .map(key -> URLEncoder.encode(key, StandardCharsets.UTF_8))
                    .collect(Collectors.joining(","));

            String url = UriComponentsBuilder
                    .fromHttpUrl(productConfig.getBaseUrl())
                    .path("/measures/component")
                    .queryParam("component", projectKey)
                    .queryParam("metricKeys", encodedMetrics)
                    .build()
                    .toUriString();

            // Print curl command for debugging
            String curlCommand = String.format("curl -X GET '%s' -H 'Authorization: Bearer %s'", 
                url, productConfig.getToken());
            logger.info("SonarCloud measures API curl command: {}", curlCommand);

            SonarMeasuresResponse response = webClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + productConfig.getToken())
                    .retrieve()
                    .bodyToMono(SonarMeasuresResponse.class)
                    .block();

            if (response != null && response.getComponent() != null && response.getComponent().getMeasures() != null) {
                for (var measure : response.getComponent().getMeasures()) {
                    measures.put(measure.getMetric(), measure.getValue());
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching measures for project {}: {}", projectKey, e.getMessage(), e);
        }

        return measures;
    }

    public List<ProductDto> getProductsWithProjects() {
        List<ProductDto> products = new ArrayList<>();
        
        for (Map.Entry<String, ProductConfig.Product> entry : productConfig.getProducts().entrySet()) {
            ProductDto product = new ProductDto();
            product.setKey(entry.getKey());
            product.setName(entry.getValue().getName());
            
            // Get projects for this product
            List<SonarProject> projects = getProjectsByProduct(entry.getKey());
            product.setProjects(projects);
            
            products.add(product);
        }
        
        return products;
    }

    public String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
} 