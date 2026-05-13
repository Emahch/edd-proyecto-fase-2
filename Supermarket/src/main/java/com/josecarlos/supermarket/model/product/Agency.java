package com.josecarlos.supermarket.model.product;

import java.util.List;
import com.josecarlos.supermarket.model.hash.Almacenable;
import com.josecarlos.supermarket.model.lists.Queue;

/**
 *
 * @author LENOVO
 */
public class Agency implements Almacenable<Agency> {

    private String id;
    private String name;
    private String location;
    private Double enterTime;
    private Double prepareTime;
    private Double dispatchInterval;
    private Catalog catalog;
    
    private Queue<Product> enterQueue;
    private Queue<Product> prepareQueue;
    private Queue<Product> exitQueue;

    private long nextAvailable = 0;

    public Agency(String id, String name, String location, Double enterTime, Double prepareTime, Double dispatchInterval) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.enterTime = enterTime;
        this.prepareTime = prepareTime;
        this.dispatchInterval = dispatchInterval;
        this.catalog = new Catalog();
        this.enterQueue = new Queue<>();
        this.prepareQueue = new Queue<>();
        this.exitQueue = new Queue<>();
    }
    
    public Agency(String id, String name, String location, Double enterTime, Double prepareTime, Double dispatchInterval, Agency old) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.enterTime = enterTime;
        this.prepareTime = prepareTime;
        this.dispatchInterval = dispatchInterval;
        this.catalog = old.getCatalog();
        this.enterQueue = old.getEnterQueue();
        this.prepareQueue = old.getPrepareQueue();
        this.exitQueue = old.getExitQueue();
        this.nextAvailable = old.getNextAvailable();
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

    public Double getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(Double enterTime) {
        this.enterTime = enterTime;
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

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Queue<Product> getEnterQueue() {
        return enterQueue;
    }

    public Queue<Product> getPrepareQueue() {
        return prepareQueue;
    }

    public Queue<Product> getExitQueue() {
        return exitQueue;
    }

    public long getNextAvailable() {
        return nextAvailable;
    }

    
}
