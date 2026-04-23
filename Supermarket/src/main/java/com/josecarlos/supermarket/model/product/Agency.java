package com.josecarlos.supermarket.model.product;

import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Agency implements Comparable<Agency> {
    private int id;
    private String name;
    private String location;
    private String startTime;
    private String prepareTime;
    private String dispatchInterval;
    private List dispatchs;

    @Override
    public int compareTo(Agency o) {
        return this.id - o.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPrepareTime() {
        return prepareTime;
    }

    public void setPrepareTime(String prepareTime) {
        this.prepareTime = prepareTime;
    }

    public String getDispatchInterval() {
        return dispatchInterval;
    }

    public void setDispatchInterval(String dispatchInterval) {
        this.dispatchInterval = dispatchInterval;
    }

    public List getDispatchs() {
        return dispatchs;
    }

    public void setDispatchs(List dispatchs) {
        this.dispatchs = dispatchs;
    }
    
    
    
}
