package com.compliance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SonarMetricsResponse {
    private List<SonarMetric> metrics;
    private int total;
    private int p;
    private int ps;

    public List<SonarMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<SonarMetric> metrics) {
        this.metrics = metrics;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getPs() {
        return ps;
    }

    public void setPs(int ps) {
        this.ps = ps;
    }
} 