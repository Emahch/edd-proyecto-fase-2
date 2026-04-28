package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.hash.HashTable;
import com.josecarlos.supermarket.model.product.Edge;

/**
 *
 * @author LENOVO
 */
public class Graph<T> {

    private HashTable<T, Edge<T>> adjacencyList;

    public Graph() {
        this.adjacencyList = new HashTable<>(503);
    }

    public void addVertex(T vertex) {
        adjacencyList.put(vertex, null);
    }

    public void addEdge(T source, T destination, double time, double price) {
        addVertex(source);
        addVertex(destination);

        adjacencyList.put(source, new Edge<>(destination, time, price));
        adjacencyList.put(destination, new Edge<>(source, time, price));
    }

}
