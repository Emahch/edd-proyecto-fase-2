package com.josecarlos.supermarket.model.benchmark;

/**
 *
 * @author LENOVO
 */
public class BenchmarkResult {

    private long time;
    private boolean success;
    private String type;
    private String structure;

    public BenchmarkResult() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    @Override
    public String toString() {
        return String.format(" %s : %s, Tiempo: %d ns -- %s ", type, structure, time, success ? "Realizado" : "Error");
    }
    
    
}
