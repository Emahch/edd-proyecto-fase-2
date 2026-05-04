package com.josecarlos.supermarket.model.product;

import java.util.List;
import com.josecarlos.supermarket.model.hash.Almacenable;

/**
 *
 * @author LENOVO
 */
public class Agency implements Almacenable<Agency> {

    private String id;
    private String name;
    private String location;
    private Double startTime;
    private Double prepareTime;
    private Double dispatchInterval;
    private List dispatchs;
    private Catalog catalog;

    public Agency(String id, String name, String location, Double startTime, Double prepareTime, Double dispatchInterval) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.prepareTime = prepareTime;
        this.dispatchInterval = dispatchInterval;
        this.catalog = new Catalog();
    }

    @Override
    public String getKey() {
        return id;
    }

    @Override
    public int compareTo(Agency o) {
        return this.getKey().compareTo(o.getKey());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getStartTime() {
        return startTime;
    }

    public void setStartTime(Double startTime) {
        this.startTime = startTime;
    }

    public Double getPrepareTime() {
        return prepareTime;
    }

    public void setPrepareTime(Double prepareTime) {
        this.prepareTime = prepareTime;
    }

    public Double getDispatchInterval() {
        return dispatchInterval;
    }

    public void setDispatchInterval(Double dispatchInterval) {
        this.dispatchInterval = dispatchInterval;
    }

    public List getDispatchs() {
        return dispatchs;
    }

    public void setDispatchs(List dispatchs) {
        this.dispatchs = dispatchs;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

}
