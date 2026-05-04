package com.josecarlos.supermarket.model.graphs;

import com.josecarlos.supermarket.model.lists.AgenciesList;
import com.josecarlos.supermarket.model.lists.DoubleNode;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.services.ExporterSVG;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    public void updateAgency(Agency agency) {
        getById(agency.getKey()).ifPresent((t) -> t.setAgency(agency));
    }

    public boolean addEdge(Agency source, Agency destination, double time, double price) {
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

        return sourceVertex.get().addDestination(destinationVertex.get(), time, price);
    }

    public boolean exists(Agency key) {
        return adjacencyList.getById(key.getKey()).isPresent();
    }

    public Optional<Vertex> getById(String key) {
        return adjacencyList.getById(key);
    }

    public AgenciesList<Vertex> getAdjacencyList() {
        return adjacencyList;
    }

    public boolean deleteAgency(Agency agency) {
        Optional<Vertex> agencyToRemove = getById(agency.getKey());
        if (agencyToRemove.isEmpty()) {
            return false;
        }
        return adjacencyList.remove(agencyToRemove.get());
    }

    public void generateGraphviz(String folderPath) {
        String baseName = folderPath + File.separator + "grafo_rutas";
        File dotFile = new File(baseName + ".dot");

        try (PrintWriter pw = new PrintWriter(new FileWriter(dotFile))) {
            pw.println("digraph RutaAgencias {");
            pw.println("    rankdir=LR;");
            pw.println("    node [shape=circle, fontname=\"Arial\", style=filled, fillcolor=\"#D5F5E3\"];");
            pw.println("    edge [fontname=\"Arial\", fontsize=10];");

            DoubleNode<Vertex> current = adjacencyList.getHead();

            while (current != null) {
                Vertex sourceVertex = current.getValue();
                String sourceKey = sourceVertex.getAgency().getKey();
                String sourceName = sourceVertex.getAgency().getName().replace("\"", "\\\"");

                pw.println("    \"" + sourceKey + "\" [label=\"" + sourceName + "\"];");

                DoubleNode<Edge<Vertex>> head = sourceVertex.getDestionations().getHead();
                while (head != null) {
                    Edge<Vertex> edge = head.getValue();
                    String destKey = edge.getDestination().getAgency().getName();

                    String edgeLabel = String.format("T: %.1fh\n$: %.2f", edge.getTime(), edge.getPrice());

                    pw.println("    \"" + sourceKey + "\" -> \"" + destKey + "\" [label=\"" + edgeLabel + "\", color=\"#2E86C1\"];");
                    head = head.getNext();
                }

                current = current.getNext();
            }

            pw.println("}");
            System.out.println("DOT del Grafo generado en: " + dotFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo DOT del grafo: " + e.getMessage());
        }

        ExporterSVG.exportToSvg(baseName);
    }
}
