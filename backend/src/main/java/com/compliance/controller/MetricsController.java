package com.compliance.controller;

import com.compliance.dto.SonarMetric;
import com.compliance.dto.SonarProject;
import com.compliance.dto.SnykProject;
import com.compliance.dto.SnykProjectDetails;
import com.compliance.dto.ProductDto;
import com.compliance.service.ExcelExportService;
import com.compliance.service.SonarCloudService;
import com.compliance.service.SnykService;
import com.compliance.service.ComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

@Controller
public class MetricsController {
    
    @Autowired
    private SonarCloudService sonarCloudService;
    
    @Autowired
    private SnykService snykService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    @Autowired
    private ComparisonService comparisonService;
    
    @GetMapping("/")
    public String index(Model model) {
        try {
            System.out.println("Loading metrics and products...");
            
            // SonarCloud data
            List<SonarMetric> sonarMetrics = sonarCloudService.getAllMetrics();
            System.out.println("Loaded " + sonarMetrics.size() + " SonarCloud metrics");
            
            List<ProductDto> sonarProducts = sonarCloudService.getProductsWithProjects();
            System.out.println("Loaded " + sonarProducts.size() + " SonarCloud products");
            
            // Snyk data
            List<ProductDto> snykProducts = snykService.getProducts();
            System.out.println("Loaded " + snykProducts.size() + " Snyk products");
            
            model.addAttribute("sonarMetrics", sonarMetrics);
            model.addAttribute("sonarProducts", sonarProducts);
            model.addAttribute("snykProducts", snykProducts);
            System.out.println("Added to model - sonarMetrics: " + sonarMetrics.size() + 
                ", sonarProducts: " + sonarProducts.size() + ", snykProducts: " + snykProducts.size());
        } catch (Exception e) {
            System.err.println("Error in controller: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Failed to load data: " + e.getMessage());
        }
        return "index";
    }
    
    @PostMapping("/export/sonar")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> exportSonarToExcel(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> selectedMetrics = (List<String>) request.get("metrics");
            @SuppressWarnings("unchecked")
            List<String> selectedProjects = (List<String>) request.get("projects");
            @SuppressWarnings("unchecked")
            List<String> selectedProducts = (List<String>) request.get("products");
            
            List<String> projectsToExport = new ArrayList<>();
            
            // If products are selected, get all projects for those products
            if (selectedProducts != null && !selectedProducts.isEmpty()) {
                for (String productKey : selectedProducts) {
                    List<SonarProject> productProjects = sonarCloudService.getProjectsByProduct(productKey);
                    projectsToExport.addAll(productProjects.stream()
                            .map(SonarProject::getKey)
                            .collect(java.util.stream.Collectors.toList()));
                }
            }
            
            // Add individually selected projects
            if (selectedProjects != null) {
                projectsToExport.addAll(selectedProjects);
            }
            
            // Remove duplicates
            projectsToExport = projectsToExport.stream().distinct().collect(java.util.stream.Collectors.toList());
            
            byte[] excelData = excelExportService.exportSonarMetrics(projectsToExport, selectedMetrics);
            
            ByteArrayResource resource = new ByteArrayResource(excelData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sonarcloud-metrics.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelData.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/export/snyk")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> exportSnykToExcel(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> selectedProjects = (List<String>) request.get("projects");
            @SuppressWarnings("unchecked")
            List<String> selectedProducts = (List<String>) request.get("products");
            
            List<String> projectsToExport = new ArrayList<>();
            
            // If products are selected, get all projects for those products
            if (selectedProducts != null && !selectedProducts.isEmpty()) {
                for (String productKey : selectedProducts) {
                    List<SnykProject> productProjects = snykService.getProjectsByProduct(productKey);
                    projectsToExport.addAll(productProjects.stream()
                            .map(SnykProject::getId)
                            .collect(java.util.stream.Collectors.toList()));
                }
            }
            
            // Add individually selected projects
            if (selectedProjects != null) {
                projectsToExport.addAll(selectedProjects);
            }
            
            // Remove duplicates
            projectsToExport = projectsToExport.stream().distinct().collect(java.util.stream.Collectors.toList());
            
            byte[] excelData = excelExportService.exportSnykMetrics(projectsToExport);
            
            ByteArrayResource resource = new ByteArrayResource(excelData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=snyk-metrics.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelData.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/display/sonar")
    @ResponseBody
    public Map<String, Object> displaySonarMetrics(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<String> selectedMetrics = (List<String>) request.get("metrics");
            @SuppressWarnings("unchecked")
            List<String> selectedProjects = (List<String>) request.get("projects");
            @SuppressWarnings("unchecked")
            List<String> selectedProducts = (List<String>) request.get("products");
            
            List<String> projectsToDisplay = new ArrayList<>();
            
            // If products are selected, get all projects for those products
            if (selectedProducts != null && !selectedProducts.isEmpty()) {
                for (String productKey : selectedProducts) {
                    List<SonarProject> productProjects = sonarCloudService.getProjectsByProduct(productKey);
                    projectsToDisplay.addAll(productProjects.stream()
                            .map(SonarProject::getKey)
                            .collect(java.util.stream.Collectors.toList()));
                }
            }
            
            // Add individually selected projects
            if (selectedProjects != null) {
                projectsToDisplay.addAll(selectedProjects);
            }
            
            // Remove duplicates
            projectsToDisplay = projectsToDisplay.stream().distinct().collect(java.util.stream.Collectors.toList());
            
            // Get metrics data
            List<Map<String, Object>> data = new ArrayList<>();
            String timestamp = sonarCloudService.getCurrentTimestamp();
            
            for (String projectKey : projectsToDisplay) {
                Map<String, Object> projectData = new HashMap<>();
                projectData.put("project", projectKey);
                projectData.put("createdAt", timestamp);
                
                Map<String, String> measures = sonarCloudService.getProjectMeasures(projectKey, selectedMetrics);
                for (String metric : selectedMetrics) {
                    projectData.put(metric, measures.getOrDefault(metric, "N/A"));
                }
                
                data.add(projectData);
            }
            
            response.put("success", true);
            response.put("data", data);
            response.put("timestamp", timestamp);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    @PostMapping("/display/snyk")
    @ResponseBody
    public Map<String, Object> displaySnykMetrics(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<String> selectedProjects = (List<String>) request.get("projects");
            @SuppressWarnings("unchecked")
            List<String> selectedProducts = (List<String>) request.get("products");
            
            List<String> projectsToDisplay = new ArrayList<>();
            
            // If products are selected, get all projects for those products
            if (selectedProducts != null && !selectedProducts.isEmpty()) {
                for (String productKey : selectedProducts) {
                    List<SnykProject> productProjects = snykService.getProjectsByProduct(productKey);
                    projectsToDisplay.addAll(productProjects.stream()
                            .map(SnykProject::getId)
                            .collect(java.util.stream.Collectors.toList()));
                }
            }
            
            // Add individually selected projects
            if (selectedProjects != null) {
                projectsToDisplay.addAll(selectedProjects);
            }
            
            // Remove duplicates
            projectsToDisplay = projectsToDisplay.stream().distinct().collect(java.util.stream.Collectors.toList());
            
            // Get metrics data
            List<Map<String, Object>> data = new ArrayList<>();
            String timestamp = snykService.getCurrentTimestamp();
            
            for (String projectId : projectsToDisplay) {
                SnykProjectDetails details = snykService.getProjectDetails(projectId);
                if (details != null) {
                    Map<String, Object> projectData = new HashMap<>();
                    projectData.put("projectName", details.getName());
                    projectData.put("projectId", details.getId());
                    projectData.put("type", details.getType());
                    projectData.put("status", details.isMonitored() ? "Active" : "Inactive");
                    projectData.put("totalDependencies", details.getTotalDependencies());
                    projectData.put("createdAt", timestamp);
                    
                    if (details.getIssueCountsBySeverity() != null) {
                        projectData.put("criticalIssues", details.getIssueCountsBySeverity().getCritical());
                        projectData.put("highIssues", details.getIssueCountsBySeverity().getHigh());
                        projectData.put("mediumIssues", details.getIssueCountsBySeverity().getMedium());
                        projectData.put("lowIssues", details.getIssueCountsBySeverity().getLow());
                    } else {
                        projectData.put("criticalIssues", 0);
                        projectData.put("highIssues", 0);
                        projectData.put("mediumIssues", 0);
                        projectData.put("lowIssues", 0);
                    }
                    
                    projectData.put("lastTested", details.getLastTestedDate() != null ? details.getLastTestedDate() : "N/A");
                    projectData.put("testFrequency", details.getTestFrequency() != null ? details.getTestFrequency() : "N/A");
                    projectData.put("branch", details.getBranch() != null ? details.getBranch() : "N/A");
                    projectData.put("repositoryUrl", details.getRemoteRepoUrl() != null ? details.getRemoteRepoUrl() : "N/A");
                    
                    data.add(projectData);
                }
            }
            
            response.put("success", true);
            response.put("data", data);
            response.put("timestamp", timestamp);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return response;
    }
    
    // SonarCloud API endpoints
    @GetMapping("/api/sonar/metrics")
    @ResponseBody
    public List<SonarMetric> getSonarMetrics() {
        return sonarCloudService.getAllMetrics();
    }
    
    @GetMapping("/api/sonar/projects")
    @ResponseBody
    public List<SonarProject> getSonarProjects() {
        return sonarCloudService.getAllProjects();
    }
    
    @GetMapping("/api/sonar/projects/{productKey}")
    @ResponseBody
    public List<SonarProject> getSonarProjectsByProduct(@PathVariable String productKey) {
        return sonarCloudService.getProjectsByProduct(productKey);
    }
    
    // Snyk API endpoints
    @GetMapping("/api/snyk/projects")
    @ResponseBody
    public List<SnykProject> getSnykProjects() {
        return snykService.getAllProjects();
    }
    
    @GetMapping("/api/snyk/projects/{productKey}")
    @ResponseBody
    public List<SnykProject> getSnykProjectsByProduct(@PathVariable String productKey) {
        return snykService.getProjectsByProduct(productKey);
    }
    
    @GetMapping("/api/snyk/project/{projectId}")
    @ResponseBody
    public SnykProjectDetails getSnykProjectDetails(@PathVariable String projectId) {
        return snykService.getProjectDetails(projectId);
    }
    
    @PostMapping("/compare")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> compareExcelFiles(
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2,
            @RequestParam("file1Name") String file1Name,
            @RequestParam("file2Name") String file2Name) {
        try {
            byte[] comparisonData = comparisonService.compareExcelFiles(
                file1.getBytes(), file2.getBytes(), file1Name, file2Name);
            
            ByteArrayResource resource = new ByteArrayResource(comparisonData);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comparison.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(comparisonData.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 