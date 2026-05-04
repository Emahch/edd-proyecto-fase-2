package com.josecarlos.supermarket.services;

import java.io.IOException;

/**
 *
 * @author LENOVO
 */
public class ExporterSVG {

    public static void exportToSvg(String fileName) {
    try {
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tsvg", fileName.concat(".dot"), "-o", fileName.concat(".svg"));
        pb.start();
        System.out.println("SVG generado exitosamente: " + fileName);
    } catch (IOException e) {
        System.err.println("Error al ejecutar Graphviz: " + e.getMessage());
    }
}
}
