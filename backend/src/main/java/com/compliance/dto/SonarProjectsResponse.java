package com.compliance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SonarProjectsResponse {
    @JsonProperty("components")
    private SonarProject[] components;
    
    private Paging paging;

    public SonarProject[] getComponents() { return components; }
    public void setComponents(SonarProject[] components) { this.components = components; }
    
    public Paging getPaging() { return paging; }
    public void setPaging(Paging paging) { this.paging = paging; }
    
    public static class Paging {
        private int pageIndex;
        private int pageSize;
        private int total;
        
        public int getPageIndex() { return pageIndex; }
        public void setPageIndex(int pageIndex) { this.pageIndex = pageIndex; }
        
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
} 