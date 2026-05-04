package com.josecarlos.supermarket.services;

import com.josecarlos.supermarket.model.graphs.Edge;
import com.josecarlos.supermarket.model.graphs.Graph;
import com.josecarlos.supermarket.model.graphs.Vertex;
import com.josecarlos.supermarket.model.product.Agency;
import com.josecarlos.supermarket.model.product.Product;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author LENOVO
 */
public class CSVService {

    public List<String> loadProducts(Graph graph, String filePath) {
        List<String> errors = new LinkedList<>();
        int lineCount = 1;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();

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
                    double price = Double.parseDouble(data[6].replace("\"", "").trim());
                    int stock = Integer.parseInt(data[7].replace("\"", "").trim());

                    if (barcode.isEmpty() || name.isEmpty() || category.isEmpty() || sucursalID.isEmpty() || date.isEmpty() || brand.isEmpty()) {
                        errors.add("No pueden ir espacios en blanco, linea: " + lineCount);
                        continue;
                    }
                    if (!date.matches("^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$")) {
                        errors.add("La fecha debe tener el formato yyyy-MM-dd (linea " + lineCount + ")");
                        continue;
                    }
                    if (price < 0 || stock < 0) {
                        errors.add("No pueden haber numeros negativos (linea " + lineCount + ")");
                        continue;
                    }

                    Product newProduct = new Product(name, barcode, category, date, brand, price, stock);

                    Optional<Vertex> target = graph.getById(sucursalID);
                    if (target.isPresent()) {
                        if (!target.get().getAgency().getCatalog().onProductCreated(newProduct)) {
                            errors.add("El producto esta duplicado: " + name + ", (linea " + lineCount + ")");
                        }
                    } else {
                        errors.add("La sucursal no existe, id: " + sucursalID + ", (linea " + lineCount + ")");
                    }
                } else {
                    errors.add("La linea no cuenta con la cantidad de datos solicitados (8) , (linea " + lineCount + ")");
                }
            }
        } catch (NumberFormatException e) {
            errors.add("Error en la linea " + lineCount + ", se esperaba un numero");
        } catch (IOException e) {
            errors.add("No se pudo leer el archivo");
        }
        return errors;
    }
    
    public List<String> loadAgencies(Graph graph, String filePath) {
        List<String> errors = new LinkedList<>();
        int lineCount = 1;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            

            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length == 6) {
                    String id = data[0].replace("\"", "").trim();
                    String name = data[1].replace("\"", "").trim();
                    String location = data[2].replace("\"", "").trim();
                    double startTime = Double.parseDouble(data[3].replace("\"", "").trim());
                    double prepareTime = Double.parseDouble(data[4].replace("\"", "").trim());
                    double dispatchTime = Double.parseDouble(data[5].replace("\"", "").trim());

                    if (id.isEmpty() || name.isEmpty() || location.isEmpty()) {
                        errors.add("No pueden ir espacios en blanco, linea: " + lineCount);
                        continue;
                    }
                    if (startTime <= 0 || prepareTime <= 0 || dispatchTime <= 0) {
                        errors.add("No pueden haber numeros negativos o iguales a 0 (linea " + lineCount + ")");
                        continue;
                    }

                    Agency newAgency = new Agency(id, name, location, startTime, prepareTime, dispatchTime);
                    
                    if (graph.exists(newAgency)) {
                            errors.add("La Sucursal esta duplicada: " + name + ", (linea " + lineCount + ")");
                    } else {
                        graph.addVertex(newAgency);
                    }
                } else {
                    errors.add("La linea no cuenta con la cantidad de datos solicitados (6) , (linea " + lineCount + ")");
                }
            }
        } catch (NumberFormatException e) {
            errors.add("Error en la linea " + lineCount + ", se esperaba un numero");
        } catch (IOException e) {
            errors.add("No se pudo leer el archivo");
        }
        return errors;
    }
    
    public List<String> loadConnections(Graph graph, String filePath) {
        List<String> errors = new LinkedList<>();
        int lineCount = 1;
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            

            while ((line = br.readLine()) != null) {
                lineCount++;
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (data.length == 4) {
                    String sourceId = data[0].replace("\"", "").trim();
                    String destinationId = data[1].replace("\"", "").trim();
                    double time = Double.parseDouble(data[2].replace("\"", "").trim());
                    double price = Double.parseDouble(data[3].replace("\"", "").trim());

                    if (sourceId.isEmpty() || destinationId.isEmpty()) {
                        errors.add("No pueden ir espacios en blanco, linea: " + lineCount);
                        continue;
                    }
                    if (time <= 0 || price <= 0) {
                        errors.add("No pueden haber numeros negativos o iguales a 0 (linea " + lineCount + ")");
                        continue;
                    }

                    Optional<Vertex> source = graph.getById(sourceId);
                    Optional<Vertex> destination = graph.getById(destinationId);
                    
                    if (source.isEmpty()) {
                        errors.add("La Sucursal de origen no existe: " + sourceId + ", (linea " + lineCount + ")");
                        continue;
                    }
                    if (destination.isEmpty()) {
                        errors.add("La Sucursal de destino no existe: " + destinationId + ", (linea " + lineCount + ")");
                        continue;
                    }
                    
                    if (!graph.addEdge(source.get().getAgency(), destination.get().getAgency(), time, price)) {
                        errors.add("Ya existe una conexion entre " + sourceId + " y " + destinationId + ", (linea " + lineCount + ")");
                    }
                } else {
                    errors.add("La linea no cuenta con la cantidad de datos solicitados (4) , (linea " + lineCount + ")");
                }
            }
        } catch (NumberFormatException e) {
            errors.add("Error en la linea " + lineCount + ", se esperaba un numero");
        } catch (IOException e) {
            errors.add("No se pudo leer el archivo");
        }
        return errors;
    }
    
    
}
