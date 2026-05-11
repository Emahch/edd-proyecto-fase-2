package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.lists.DoubleNode;
import com.josecarlos.supermarket.model.product.Agency;
import java.util.*;

public class Dijkstra {

    public Optional<PathResult> shortestPath(Graph graph, String sourceKey, String destKey, ComparationMode mode) {
        Optional<Vertex> sourceOpt = graph.getById(sourceKey);
        Optional<Vertex> destOpt = graph.getById(destKey);

        if (sourceOpt.isEmpty() || destOpt.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();

        PriorityQueue<PQEntry> queue = new PriorityQueue<>(
                Comparator.comparingDouble(PQEntry::getCost)
        );

        DoubleNode<Vertex> head = graph.getAdjacencyList().getHead();
        while (head != null) {
            dist.put(head.getValue().getKey(), Double.POSITIVE_INFINITY);
            head = head.getNext();
        }

        dist.put(sourceKey, 0.0);
        queue.add(new PQEntry(0.0, sourceKey));

        while (!queue.isEmpty()) {
            PQEntry current = queue.poll();

            if (visited.contains(current.getKey())) {
                continue;
            }
            visited.add(current.getKey());

            if (current.getKey().equals(destKey)) {
                break;
            }
            Optional<Vertex> currentVertexOpt = graph.getById(current.getKey());
            if (currentVertexOpt.isEmpty()) {
                continue;
            }
            Vertex currentVertex = currentVertexOpt.get();

            DoubleNode<Edge<Vertex>> headEdge = currentVertex.getDestionations().getHead();
            while (headEdge != null) {
                Edge<Vertex> edge = headEdge.getValue();
                String neighborKey = edge.getKey();
                headEdge = headEdge.getNext();
                if (visited.contains(neighborKey)) {
                    continue;
                }

                double weight = (mode == ComparationMode.TIME) ? (edge.getTime() + currentVertex.getAgency().getEnterTime()) : edge.getPrice();
                double newDist = dist.get(current.getKey()) + weight;

                if (newDist < dist.getOrDefault(neighborKey, Double.POSITIVE_INFINITY)) {
                    dist.put(neighborKey, newDist);
                    prev.put(neighborKey, current.getKey());
                    queue.add(new PQEntry(newDist, neighborKey));
                }
            }
        }

        if (dist.get(destKey) == Double.POSITIVE_INFINITY) {
            return Optional.empty();
        }

        List<Agency> path = new ArrayList<>();
        String step = destKey;
        while (step != null) {
            Optional<Vertex> v = graph.getById(step);
            v.ifPresent(vertex -> path.add(0, vertex.getAgency()));
            step = prev.get(step);
        }

        return Optional.of(new PathResult(path, dist.get(destKey)));
    }

    public List<Vertex> getAllReachableVertices(Graph graph, String sourceKey, ComparationMode mode) {
        Optional<Vertex> sourceOpt = graph.getById(sourceKey);

        if (sourceOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Double> dist = new HashMap<>();
        Set<String> visited = new HashSet<>();
        List<Vertex> reachableVertices = new ArrayList<>();

        PriorityQueue<PQEntry> queue = new PriorityQueue<>(
                Comparator.comparingDouble(PQEntry::getCost)
        );

        DoubleNode<Vertex> head = graph.getAdjacencyList().getHead();
        while (head != null) {
            dist.put(head.getValue().getKey(), Double.POSITIVE_INFINITY);
            head = head.getNext();
        }

        dist.put(sourceKey, 0.0);
        queue.add(new PQEntry(0.0, sourceKey));

        while (!queue.isEmpty()) {
            PQEntry current = queue.poll();

            if (visited.contains(current.getKey())) {
                continue;
            }

            visited.add(current.getKey());

            Optional<Vertex> currentVertexOpt = graph.getById(current.getKey());
            if (currentVertexOpt.isEmpty()) {
                continue;
            }

            Vertex currentVertex = currentVertexOpt.get();
            reachableVertices.add(currentVertex);

            DoubleNode<Edge<Vertex>> headEdge = currentVertex.getDestionations().getHead();
            while (headEdge != null) {
                Edge<Vertex> edge = headEdge.getValue();
                String neighborKey = edge.getKey();

                if (!visited.contains(neighborKey)) {
                    double weight = (mode == ComparationMode.TIME)
                            ? (edge.getTime() + currentVertex.getAgency().getEnterTime())
                            : edge.getPrice();

                    double newDist = dist.get(current.getKey()) + weight;

                    if (newDist < dist.getOrDefault(neighborKey, Double.POSITIVE_INFINITY)) {
                        dist.put(neighborKey, newDist);
                        queue.add(new PQEntry(newDist, neighborKey));
                    }
                }
                headEdge = headEdge.getNext();
            }
        }

        return reachableVertices;
    }
}
