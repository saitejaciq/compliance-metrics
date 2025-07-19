package com.compliance.service;

import com.compliance.config.SnykConfig;
import com.compliance.dto.SnykProject;
import com.compliance.dto.SnykProjectsResponse;
import com.compliance.dto.SnykProjectDetails;
import com.compliance.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SnykService {
    private static final Logger logger = LoggerFactory.getLogger(SnykService.class);

    @Autowired
    private SnykConfig snykConfig;

    @Autowired
    private WebClient webClient;

    public List<ProductDto> getProducts() {
        List<ProductDto> products = new ArrayList<>();
        for (Map.Entry<String, SnykConfig.Product> entry : snykConfig.getProducts().entrySet()) {
            ProductDto product = new ProductDto();
            product.setKey(entry.getKey());
            product.setName(entry.getValue().getName());
            products.add(product);
        }
        return products;
    }

    public List<SnykProject> getProjectsByProduct(String productKey) {
        SnykConfig.Product product = snykConfig.getProducts().get(productKey);
        if (product == null) {
            logger.error("Product not found: {}", productKey);
            return new ArrayList<>();
        }

                try {
            // Use URI.create to avoid any additional encoding
            String encodedTag = java.net.URLEncoder.encode(product.getTag(), java.nio.charset.StandardCharsets.UTF_8);
            String urlString = snykConfig.getBaseUrl() + "/rest/orgs/" + snykConfig.getOrganization() + "/projects?version=" + snykConfig.getApiVersion() + "&tags=" + encodedTag+"&limit=100";

            SnykProjectsResponse response = webClient.get()
                    .uri(URI.create(urlString))
                    .header("Authorization", "token " + snykConfig.getToken())
                    .header("Accept", "application/vnd.api+json")
                    .retrieve()
                    .bodyToMono(SnykProjectsResponse.class)
                    .block();

            // Print curl command for debugging (build URL manually for logging)
            String baseUrl = snykConfig.getBaseUrl() + "/rest/orgs/" + snykConfig.getOrganization() + "/projects";
            String url = baseUrl + "?version=" + snykConfig.getApiVersion() + "&tags=" + encodedTag;
            String curlCommand = String.format("curl -X GET '%s' -H 'Authorization: token %s' -H 'Accept: application/vnd.api+json'",
                url, snykConfig.getToken());
            logger.info("Snyk API curl command: {}", curlCommand);

            if (response != null && response.getData() != null) {
                logger.info("Retrieved {} Snyk projects for product: {}",
                    response.getData().size(), productKey);
                return response.getData();
            }
        } catch (Exception e) {
            logger.error("Error fetching Snyk projects for product {}: {}", productKey, e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    public List<SnykProject> getAllProjects() {
        List<SnykProject> allProjects = new ArrayList<>();

        for (String productKey : snykConfig.getProducts().keySet()) {
            List<SnykProject> projects = getProjectsByProduct(productKey);
            allProjects.addAll(projects);
        }

        logger.info("Retrieved {} total Snyk projects across all products", allProjects.size());
        return allProjects;
    }

    public SnykProjectDetails getProjectDetails(String projectId) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(snykConfig.getBaseUrl())
                    .path("/v1/org/{orgId}/project/{projectId}")
                    .buildAndExpand(snykConfig.getOrganization(), projectId)
                    .toUriString();

            // Print curl command for debugging
            String curlCommand = String.format("curl -X GET '%s' -H 'Authorization: token %s' -H 'Accept: */*'",
                url, snykConfig.getToken());
            logger.info("Snyk project details API curl command: {}", curlCommand);

            SnykProjectDetails details = webClient.get()
                    .uri(url)
                    .header("Authorization", "token " + snykConfig.getToken())
                    .header("Accept", "*/*")
                    .retrieve()
                    .bodyToMono(SnykProjectDetails.class)
                    .block();

            if (details != null) {
                logger.info("Retrieved Snyk project details for project: {}", projectId);
                return details;
            }
        } catch (Exception e) {
            logger.error("Error fetching Snyk project details for project {}: {}", projectId, e.getMessage(), e);
        }

        return null;
    }

    public List<SnykProjectDetails> getProjectsDetails(List<String> projectIds) {
        List<SnykProjectDetails> details = new ArrayList<>();

        for (String projectId : projectIds) {
            SnykProjectDetails detail = getProjectDetails(projectId);
            if (detail != null) {
                details.add(detail);
            }
        }

        logger.info("Retrieved details for {} Snyk projects", details.size());
        return details;
    }

    public List<SnykProjectDetails> getProjectsDetailsByProduct(String productKey) {
        List<SnykProject> projects = getProjectsByProduct(productKey);
        List<String> projectIds = projects.stream()
                .map(SnykProject::getId)
                .collect(Collectors.toList());

        return getProjectsDetails(projectIds);
    }

    public String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
