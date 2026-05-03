package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.lists.AgenciesList;
import com.josecarlos.supermarket.model.product.Agency;
import java.util.Optional;

/**
 *
 * @author LENOVO
 */
public class Graph {

    private AgenciesList<Vertex> adjacencyList;

    public Graph() {
        this.adjacencyList = new AgenciesList<>();
    }
    
    public boolean addVertex(Agency agency) {
        return adjacencyList.add(new Vertex(agency));
    }
    
    public boolean addVertex(Vertex vertex) {
        return adjacencyList.add(vertex);
    }

    public void addEdge(Agency source, Agency destination, double time, double price) {
        Optional<Vertex> sourceVertex = adjacencyList.getById(source.getKey());
        if (sourceVertex.isEmpty()) {
            sourceVertex = Optional.of(new Vertex(source));
            addVertex(sourceVertex.get());
        }
        Optional<Vertex> destinationVertex = adjacencyList.getById(destination.getKey());
        if (destinationVertex.isEmpty()) {
            destinationVertex = Optional.of(new Vertex(destination));
            addVertex(destinationVertex.get());
        }
        
        sourceVertex.get().addDestination(destinationVertex.get(), time, price);
    }

    public boolean exists(Agency key) {
        return adjacencyList.getById(key.getKey()).isPresent();
    }

    public AgenciesList<Vertex> getAdjacencyList() {
        return adjacencyList;
    }

}
