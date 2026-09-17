package com.example.pharmastock.dto;

import java.util.List;

public class BulkImportResult {
    private int successCount;
    private List<String> errors;

    public BulkImportResult(int successCount, List<String> errors) {
        this.successCount = successCount;
        this.errors = errors;
    }

    public int getSuccessCount() { return successCount; }
    public List<String> getErrors() { return errors; }
}
