package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.product.Edge;
import java.util.*;

/**
 *
 * @author LENOVO
 */
public class Graph<T> {

    private Map<T, List<Edge<T>>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashMap<>();
    }

    public void addVertex(T vertex) {
        adjacencyList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(T source, T destination, double time, double price) {
        addVertex(source);
        addVertex(destination);

        adjacencyList.get(source).add(new Edge<>(destination, time, price));
        adjacencyList.get(destination).add(new Edge<>(source, time, price));
    }

}
