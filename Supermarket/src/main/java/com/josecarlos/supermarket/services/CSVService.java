package com.josecarlos.supermarket.services;

import com.josecarlos.supermarket.model.csv.CSVResponse;
import com.josecarlos.supermarket.model.graphs.Graph;
import com.josecarlos.supermarket.model.graphs.Vertex;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.model.product.Product;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author LENOVO
 */
public class CSVService {

    public CSVResponse loadProducts(Graph graph, String filePath) {
        CSVResponse response = new CSVResponse();
        int lineCount = 0;
        int loadedProducts = 0;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            lineCount++;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length == 8) {
                    String sucursalID = data[0].replace("\"", "").trim();
                    String name = data[1].replace("\"", "").trim();
                    String barcode = data[2].replace("\"", "").trim();
                    String category = data[3].replace("\"", "").trim();
                    String date = data[4].replace("\"", "").trim();
                    String brand = data[5].replace("\"", "").trim();
                    double price;
                    try {
                        price = Double.parseDouble(data[6].replace("\"", "").trim());
                    } catch (NumberFormatException e) {
                        response.addError("Precio no es un numero (linea " + lineCount + ")");
                        continue;
                    }
                    if (price < 0) {
                        response.addError("Pecio es menor a 0 (linea " + lineCount + ")");
                        continue;
                    }
                    int stock;
                    try {
                        stock = Integer.parseInt(data[7].replace("\"", "").trim());
                    } catch (NumberFormatException e) {
                        response.addError("Stock no es un numero (linea " + lineCount + ")");
                        continue;
                    }
                    if (stock < 0) {
                        response.addError("Stck es menor a 0 (linea " + lineCount + ")");
                        continue;
                    }

                    if (barcode.isEmpty() || name.isEmpty() || category.isEmpty() || sucursalID.isEmpty() || date.isEmpty() || brand.isEmpty()) {
                        response.addError("Espacios en blanco (linea: " + lineCount + ")");
                        continue;
                    }
                    if (!date.matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) {
                        response.addError("La fecha debe tener el formato yyyy-MM-dd (linea " + lineCount + ")");
                        continue;
                    }

                    Product newProduct = new Product(name, barcode, category, date, brand, price, stock);

                    Optional<Vertex> target = graph.getById(sucursalID);
                    if (target.isPresent()) {
                        if (!target.get().getAgency().getCatalog().onProductCreated(newProduct)) {
                            response.addError("El producto esta duplicado: " + name + " (linea " + lineCount + ")");
                        } else {
                            loadedProducts++;
                        }
                    } else {
                        response.addError("La sucursal no existe, id: " + sucursalID + " (linea " + lineCount + ")");
                    }
                } else {
                    response.addError("La linea no cuenta con la cantidad de datos solicitados (8) (linea " + lineCount + ")");
                }
            }
        } catch (IOException e) {
            response.addError("No se pudo leer el archivo");
        }
        response.setSuccess(loadedProducts);
        response.setTotalCount(lineCount);
        return response;
    }

    public CSVResponse loadAgencies(Graph graph, String filePath) {
        CSVResponse response = new CSVResponse();
        int lineCount = 0;
        int loadedAgencies = 0;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            lineCount++;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length == 6) {
                    String id = data[0].replace("\"", "").trim();
                    String name = data[1].replace("\"", "").trim();
                    String location = data[2].replace("\"", "").trim();
                    double startTime;
                    try {
                        startTime = Double.parseDouble(data[3].replace("\"", "").trim());
                    } catch (NumberFormatException e) {
                        response.addError("Tiempo de ingreso no es un numero (linea " + lineCount + ")");
                        continue;
                    }
                    double prepareTime;
                    try {
                        prepareTime = Double.parseDouble(data[4].replace("\"", "").trim());
                    } catch (NumberFormatException e) {
                        response.addError("Tiempo de preparacion no es un numero (linea " + lineCount + ")");
                        continue;
                    }
                    double dispatchTime;
                    try {
                        dispatchTime = Double.parseDouble(data[5].replace("\"", "").trim());
                    } catch (NumberFormatException e) {
                        response.addError("Tiempo de despacho no es un numero (linea " + lineCount + ")");
                        continue;
                    }

                    if (id.isEmpty() || name.isEmpty() || location.isEmpty()) {
                        response.addError("Espacios en blanco (linea: " + lineCount + ")");
                        continue;
                    }
                    if (startTime <= 0 || prepareTime <= 0 || dispatchTime <= 0) {
                        response.addError("No pueden haber numeros negativos o iguales a 0 (linea " + lineCount + ")");
                        continue;
                    }

                    Agency newAgency = new Agency(id, name, location, startTime, prepareTime, dispatchTime);

                    if (graph.exists(newAgency)) {
                        response.addError("La Sucursal esta duplicada: " + name + ", (linea " + lineCount + ")");
                    } else {
                        if (graph.addVertex(newAgency)) {
                            loadedAgencies++;
                        } else {
                            response.addError("No se pudo agregar la sucursal (linea " + lineCount + ")");
                        }
                    }
                } else {
                    response.addError("La linea no cuenta con la cantidad de datos solicitados (6) , (linea " + lineCount + ")");
                }
            }
        } catch (IOException e) {
            response.addError("No se pudo leer el archivo");
        }
        response.setSuccess(loadedAgencies);
        response.setTotalCount(lineCount);
        return response;
    }

    public CSVResponse loadConnections(Graph graph, String filePath) {
        CSVResponse response = new CSVResponse();
        int lineCount = 0;
        int loadedProducts = 0;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            lineCount++;
            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length == 4) {
                    String sourceId = data[0].replace("\"", "").trim();
                    String destinationId = data[1].replace("\"", "").trim();
                    double time;
                    try {
                        time = Double.parseDouble(data[2].replace("\"", "").trim());
                    } catch (NumberFormatException e) {
                        response.addError("Tiempo de traspaso no es un numero (linea " + lineCount + ")");
                        continue;
                    }
                    double price;
                    try {
                        price = Double.parseDouble(data[3].replace("\"", "").trim());
                    } catch (NumberFormatException e) {
                        response.addError("Precio de traspaso no es un numero (linea " + lineCount + ")");
                        continue;
                    }

                    if (sourceId.isEmpty() || destinationId.isEmpty()) {
                        response.addError("Espacios en blanco (linea " + lineCount + ")");
                        continue;
                    }
                    if (time <= 0 || price <= 0) {
                        response.addError("No pueden haber numeros negativos o iguales a 0 (linea " + lineCount + ")");
                        continue;
                    }

                    Optional<Vertex> source = graph.getById(sourceId);
                    Optional<Vertex> destination = graph.getById(destinationId);

                    if (source.isEmpty()) {
                        response.addError("La Sucursal de origen no existe: " + sourceId + " (linea " + lineCount + ")");
                        continue;
                    }
                    if (destination.isEmpty()) {
                        response.addError("La Sucursal de destino no existe: " + destinationId + " (linea " + lineCount + ")");
                        continue;
                    }

                    if (!graph.addEdge(source.get().getAgency(), destination.get().getAgency(), time, price)) {
                        response.addError("Ya existe una conexion entre " + sourceId + " y " + destinationId + ", (linea " + lineCount + ")");
                    } else {
                        loadedProducts++;
                    }
                } else {
                    response.addError("La linea no cuenta con la cantidad de datos solicitados (4) , (linea " + lineCount + ")");
                }
            }
        } catch (IOException e) {
            response.addError("No se pudo leer el archivo");
        }
        response.setSuccess(loadedProducts);
        response.setTotalCount(lineCount);
        return response;
    }

}
