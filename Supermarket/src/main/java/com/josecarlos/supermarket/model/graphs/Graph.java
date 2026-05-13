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
            pw.println("    graph [");
            pw.println("        rankdir=LR,");
            pw.println("        splines=curved,");
            pw.println("        nodesep=1.0,");
            pw.println("        ranksep=1.5,");
            pw.println("        bgcolor=\"#F8F9FA\",");
            pw.println("        dpi=300,");
            pw.println("        fontname=\"Arial\",");
            pw.println("        fontsize=14");
            pw.println("    ];");
            pw.println("    ");
            pw.println("    node [");
            pw.println("        shape=box,");
            pw.println("        style=\"rounded,filled\",");
            pw.println("        fillcolor=\"#3498DB\",");
            pw.println("        fontcolor=\"white\",");
            pw.println("        fontname=\"Arial\",");
            pw.println("        fontsize=11,");
            pw.println("        width=1.5,");
            pw.println("        height=0.8");
            pw.println("    ];");
            pw.println("    ");
            pw.println("    edge [");
            pw.println("        fontname=\"Arial\",");
            pw.println("        fontsize=9,");
            pw.println("        color=\"#34495E\",");
            pw.println("        penwidth=1.5,");
            pw.println("        arrowsize=1.2");
            pw.println("    ];");
            pw.println("    ");

            DoubleNode<Vertex> current = adjacencyList.getHead();
            int agencyCount = 0;

            while (current != null) {
                agencyCount++;
                current = current.getNext();
            }

            current = adjacencyList.getHead();
            int index = 0;

            while (current != null) {
                Vertex sourceVertex = current.getValue();
                String sourceKey = sourceVertex.getAgency().getKey();
                String sourceName = sourceVertex.getAgency().getName().replace("\"", "\\\"");

                String fillColor = (index == 0) ? "#27AE60" : (index == agencyCount - 1) ? "#E74C3C" : "#3498DB";
                String label = String.format("%s\\n[%s]", sourceName, sourceKey);

                pw.println("    \"" + sourceKey + "\" [label=\"" + label + "\", fillcolor=\"" + fillColor + "\"];");

                DoubleNode<Edge<Vertex>> head = sourceVertex.getDestionations().getHead();
                while (head != null) {
                    Edge<Vertex> edge = head.getValue();
                    String destKey = edge.getDestination().getAgency().getKey();

                    String timeStr = String.format("%.1fh", edge.getTime());
                    String priceStr = String.format("$%.2f", edge.getPrice());
                    String edgeLabel = String.format("%s | %s", timeStr, priceStr);

                    pw.println("    \"" + sourceKey + "\" -> \"" + destKey + "\" [label=\"" + edgeLabel + "\"];");
                    head = head.getNext();
                }

                current = current.getNext();
                index++;
            }

            pw.println("    ");
            pw.println("    {");
            pw.println("        rank=same;");
            pw.println("        legend [");
            pw.println("            shape=plaintext,");
            pw.println("            label=<");
            pw.println("                <TABLE BORDER=\"1\" CELLBORDER=\"0\" CELLSPACING=\"4\" BGCOLOR=\"#ECF0F1\">");
            pw.println("                <TR><TD COLSPAN=\"2\" ALIGN=\"CENTER\"><B>Leyenda</B></TD></TR>");
            pw.println(
                    "                <TR><TD BGCOLOR=\"#27AE60\" WIDTH=\"20\"></TD><TD ALIGN=\"LEFT\">Sucursal Origen</TD></TR>");
            pw.println(
                    "                <TR><TD BGCOLOR=\"#3498DB\" WIDTH=\"20\"></TD><TD ALIGN=\"LEFT\">Sucursales Intermedias</TD></TR>");
            pw.println(
                    "                <TR><TD BGCOLOR=\"#E74C3C\" WIDTH=\"20\"></TD><TD ALIGN=\"LEFT\">Sucursal Destino</TD></TR>");
            pw.println("                </TABLE>");
            pw.println("            >");
            pw.println("        ];");
            pw.println("    }");

            pw.println("}");
            System.out.println("DOT del Grafo generado en: " + dotFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo DOT del grafo: " + e.getMessage());
        }

        ExporterSVG.exportToSvg(baseName);
    }
}
