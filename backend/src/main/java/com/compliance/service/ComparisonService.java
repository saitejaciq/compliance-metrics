package com.compliance.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class ComparisonService {
    
    public byte[] compareExcelFiles(byte[] file1, byte[] file2, String file1Name, String file2Name) throws Exception {
        try (Workbook workbook1 = new XSSFWorkbook(new ByteArrayInputStream(file1));
             Workbook workbook2 = new XSSFWorkbook(new ByteArrayInputStream(file2));
             Workbook comparisonWorkbook = new XSSFWorkbook()) {
            
            Sheet sheet1 = workbook1.getSheetAt(0);
            Sheet sheet2 = workbook2.getSheetAt(0);
            Sheet comparisonSheet = comparisonWorkbook.createSheet("Comparison");
            
            // Read data from both sheets
            Map<String, Map<String, String>> data1 = readSheetData(sheet1);
            Map<String, Map<String, String>> data2 = readSheetData(sheet2);
            
            // Create comparison sheet
            createComparisonSheet(comparisonSheet, data1, data2, file1Name, file2Name);
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            comparisonWorkbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    private Map<String, Map<String, String>> readSheetData(Sheet sheet) {
        Map<String, Map<String, String>> data = new HashMap<>();
        
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return data;
        
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            headers.add(cell != null ? cell.getStringCellValue() : "");
        }
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            Cell projectCell = row.getCell(0);
            if (projectCell == null) continue;
            
            String projectKey = projectCell.getStringCellValue();
            Map<String, String> projectData = new HashMap<>();
            
            for (int j = 1; j < headers.size() && j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                String value = cell != null ? getCellValueAsString(cell) : "";
                projectData.put(headers.get(j), value);
            }
            
            data.put(projectKey, projectData);
        }
        
        return data;
    }
    
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    private void createComparisonSheet(Sheet sheet, Map<String, Map<String, String>> data1, 
                                     Map<String, Map<String, String>> data2, String file1Name, String file2Name) {
        // Get all unique projects and metrics
        Set<String> allProjects = new HashSet<>();
        allProjects.addAll(data1.keySet());
        allProjects.addAll(data2.keySet());
        
        Set<String> allMetrics = new HashSet<>();
        data1.values().forEach(projectData -> allMetrics.addAll(projectData.keySet()));
        data2.values().forEach(projectData -> allMetrics.addAll(projectData.keySet()));
        
        List<String> sortedProjects = new ArrayList<>(allProjects);
        List<String> sortedMetrics = new ArrayList<>(allMetrics);
        Collections.sort(sortedProjects);
        Collections.sort(sortedMetrics);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());
        
        int colIndex = 0;
        headerRow.createCell(colIndex++).setCellValue("Project");
        
        for (String metric : sortedMetrics) {
            Cell cell = headerRow.createCell(colIndex++);
            cell.setCellValue(metric + " (" + file1Name + ")");
            cell.setCellStyle(headerStyle);
            
            cell = headerRow.createCell(colIndex++);
            cell.setCellValue(metric + " (" + file2Name + ")");
            cell.setCellStyle(headerStyle);
            
            cell = headerRow.createCell(colIndex++);
            cell.setCellValue(metric + " (Diff)");
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowIndex = 1;
        for (String project : sortedProjects) {
            Row row = sheet.createRow(rowIndex++);
            
            colIndex = 0;
            row.createCell(colIndex++).setCellValue(project);
            
            Map<String, String> projectData1 = data1.getOrDefault(project, new HashMap<>());
            Map<String, String> projectData2 = data2.getOrDefault(project, new HashMap<>());
            
            for (String metric : sortedMetrics) {
                String value1 = projectData1.getOrDefault(metric, "N/A");
                String value2 = projectData2.getOrDefault(metric, "N/A");
                
                row.createCell(colIndex++).setCellValue(value1);
                row.createCell(colIndex++).setCellValue(value2);
                
                // Calculate difference
                Cell diffCell = row.createCell(colIndex++);
                String diff = calculateDifference(value1, value2);
                diffCell.setCellValue(diff);
                
                // Color code the difference
                if (!diff.equals("No Change") && !diff.equals("N/A")) {
                    CellStyle diffStyle = createDiffStyle(sheet.getWorkbook());
                    diffCell.setCellStyle(diffStyle);
                }
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < colIndex; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private String calculateDifference(String value1, String value2) {
        if (value1.equals("N/A") || value2.equals("N/A")) {
            return "N/A";
        }
        
        if (value1.equals(value2)) {
            return "No Change";
        }
        
        try {
            double num1 = Double.parseDouble(value1);
            double num2 = Double.parseDouble(value2);
            double diff = num2 - num1;
            return String.format("%.2f", diff);
        } catch (NumberFormatException e) {
            return "Changed";
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
    
    private CellStyle createDiffStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
} 