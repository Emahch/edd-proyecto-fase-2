package com.josecarlos.supermarket.model.graphs;

/**
 *
 * @author LENOVO
 */
public class PQEntry {

    private double cost;
    private String key;

    public PQEntry(double cost, String key) {
        this.cost = cost;
        this.key = key;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    
    
}
