package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.product.Agency;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class PathResult {

    private List<Agency> path;
    private double totalCost;

    public PathResult(List<Agency> path, double totalCost){
        this.path = path;
        this.totalCost = totalCost;
    }

    public List<Agency> getPath() {
        return path;
    }

    public void setPath(List<Agency> path) {
        this.path = path;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
