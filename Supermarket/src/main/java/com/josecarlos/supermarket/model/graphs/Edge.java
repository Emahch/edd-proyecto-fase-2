package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.hash.Almacenable;
import java.text.DecimalFormat;

/**
 *
 * @author LENOVO
 * @param <T>
 */
public class Edge<T extends Almacenable<T>> implements Almacenable<Edge<T>>{

    private T destination;
    private double time;
    private double price;

    public Edge(T destination, double time, double price) {
        this.destination = destination;
        this.time = time;
        this.price = price;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00");
        return "(" + destination + ", {tiempo: " + df.format(time) + " , costo: " + df.format(price) + "} )";
    }

    public T getDestination() {
        return destination;
    }

    public void setDestination(T destination) {
        this.destination = destination;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String getKey() {
        return destination.getKey();
    }

    @Override
    public int compareTo(Edge<T> o) {
        return destination.compareTo(o.getDestination());
    }
    
    

}
