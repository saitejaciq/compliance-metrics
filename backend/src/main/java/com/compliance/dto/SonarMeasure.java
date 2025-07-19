package com.compliance.dto;

public class SonarMeasure {
    private String metric;
    private String value;
    private boolean bestValue;

    // Getters and Setters
    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public boolean isBestValue() { return bestValue; }
    public void setBestValue(boolean bestValue) { this.bestValue = bestValue; }
} 