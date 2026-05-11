package com.josecarlos.supermarket.model.csv;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class CSVResponse {
    private int totalCount;
    private int success;
    private int errorCount;
    private List<String> errors;

    public CSVResponse(int totalCount, int success, List<String> errors) {
        this.totalCount = totalCount;
        this.success = success;
        this.errors = errors;
    }

    public CSVResponse() {
        this.errors = new LinkedList<>();
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSuccess() {
        return success;
    }
    
    public void addError(String error) {
        this.errorCount++;
        this.errors.add(error);
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
