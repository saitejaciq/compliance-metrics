package com.compliance.service;

import com.compliance.dto.SonarProject;
import com.compliance.dto.SnykProjectDetails;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {
    
    @Autowired
    private SonarCloudService sonarCloudService;
    
    @Autowired
    private SnykService snykService;
    
    public byte[] exportSonarMetrics(List<String> projectKeys, List<String> metricKeys) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("SonarCloud Metrics");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            Cell projectHeader = headerRow.createCell(0);
            projectHeader.setCellValue("Project");
            projectHeader.setCellStyle(headerStyle);
            
            Cell timestampHeader = headerRow.createCell(1);
            timestampHeader.setCellValue("Created At");
            timestampHeader.setCellStyle(headerStyle);
            
            for (int i = 0; i < metricKeys.size(); i++) {
                Cell cell = headerRow.createCell(i + 2);
                cell.setCellValue(metricKeys.get(i));
                cell.setCellStyle(headerStyle);
            }
            
            // Add data rows
            int rowNum = 1;
            String currentTimestamp = sonarCloudService.getCurrentTimestamp();
            
            for (String projectKey : projectKeys) {
                Row row = sheet.createRow(rowNum++);
                
                // Project name
                row.createCell(0).setCellValue(projectKey);
                
                // Created at timestamp
                row.createCell(1).setCellValue(currentTimestamp);
                
                // Get metrics for this project
                Map<String, String> measures = sonarCloudService.getProjectMeasures(projectKey, metricKeys);
                
                // Add metric values
                for (int i = 0; i < metricKeys.size(); i++) {
                    String metricKey = metricKeys.get(i);
                    String value = measures.getOrDefault(metricKey, "N/A");
                    row.createCell(i + 2).setCellValue(value);
                }
            }
            
            // Auto-size columns
            for (int i = 0; i <= metricKeys.size() + 1; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public byte[] exportSnykMetrics(List<String> projectIds) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Snyk Metrics");
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);
            
            String[] headers = {
                "Project Name", "Project ID", "Type", "Status", "Total Dependencies",
                "Critical Issues", "High Issues", "Medium Issues", "Low Issues",
                "Last Tested", "Test Frequency", "Branch", "Repository URL", "Created At"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Add data rows
            int rowNum = 1;
            String currentTimestamp = snykService.getCurrentTimestamp();
            
            for (String projectId : projectIds) {
                SnykProjectDetails details = snykService.getProjectDetails(projectId);
                if (details != null) {
                    Row row = sheet.createRow(rowNum++);
                    
                    row.createCell(0).setCellValue(details.getName());
                    row.createCell(1).setCellValue(details.getId());
                    row.createCell(2).setCellValue(details.getType());
                    row.createCell(3).setCellValue(details.isMonitored() ? "Active" : "Inactive");
                    row.createCell(4).setCellValue(details.getTotalDependencies());
                    
                    if (details.getIssueCountsBySeverity() != null) {
                        row.createCell(5).setCellValue(details.getIssueCountsBySeverity().getCritical());
                        row.createCell(6).setCellValue(details.getIssueCountsBySeverity().getHigh());
                        row.createCell(7).setCellValue(details.getIssueCountsBySeverity().getMedium());
                        row.createCell(8).setCellValue(details.getIssueCountsBySeverity().getLow());
                    } else {
                        row.createCell(5).setCellValue(0);
                        row.createCell(6).setCellValue(0);
                        row.createCell(7).setCellValue(0);
                        row.createCell(8).setCellValue(0);
                    }
                    
                    row.createCell(9).setCellValue(details.getLastTestedDate() != null ? details.getLastTestedDate() : "N/A");
                    row.createCell(10).setCellValue(details.getTestFrequency() != null ? details.getTestFrequency() : "N/A");
                    row.createCell(11).setCellValue(details.getBranch() != null ? details.getBranch() : "N/A");
                    row.createCell(12).setCellValue(details.getRemoteRepoUrl() != null ? details.getRemoteRepoUrl() : "N/A");
                    row.createCell(13).setCellValue(currentTimestamp);
                }
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }
} 