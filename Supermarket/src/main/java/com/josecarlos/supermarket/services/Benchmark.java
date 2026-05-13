package com.josecarlos.supermarket.services;

import com.josecarlos.supermarket.model.benchmark.BenchmarkResult;
import com.josecarlos.supermarket.model.lists.Node;
import com.josecarlos.supermarket.model.lists.SimpleProductsList;
import com.josecarlos.supermarket.model.product.Catalog;
import com.josecarlos.supermarket.model.product.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author LENOVO
 */
public class Benchmark {

    public SimpleProductsList getRandomProducts(int amount, Catalog catalog) {
        SimpleProductsList catalogProducts = catalog.getSimpleList();
        if (amount >= catalog.getSimpleList().getSize()) {
            return catalogProducts;
        }
        SimpleProductsList products = new SimpleProductsList();
        
        for (int i = 0; i < amount; i++) {
            double index = Math.random() * catalogProducts.getSize();
            Product product = catalogProducts.getByIndex((int) index);
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }
    
    public List<BenchmarkResult> runSearchBenchmark(SimpleProductsList testData, Catalog catalog) {
        List<BenchmarkResult> results = new ArrayList<>(testData.getSize());
        Node<Product> current = testData.getHead();

        while (current != null) {
            Product product = current.getValue();
            results.add(runOperation(
                    p -> catalog.getSimpleList()
                            .getByBarcode(p.getBarcode())
                            .isPresent(),
                    product,
                    "Busqueda",
                    "Lista Enlazada Secuencial"
            ));
            results.add(runOperation(
                    p -> catalog.getOrderedList()
                            .getByBarcode(p.getBarcode())
                            .isPresent(),
                    product,
                    "Busqueda",
                    "Lista Enlazada Ordenada"
            ));
            results.add(runOperation(
                    p -> !catalog.getAvlTree()
                            .searchByName(p.getName()).isEmpty(),
                    product,
                    "Busqueda",
                    "Arbol AVL"
            ));
            results.add(runOperation(
                    p -> !catalog.getbTree()
                            .searchByExpiryDateRange(p.getExpireDate(), p.getExpireDate()).isEmpty(),
                    product,
                    "Busqueda",
                    "Arbol B"
            ));
            results.add(runOperation(
                    p -> !catalog.getbPlusTree()
                            .search(p).isEmpty(),
                    product,
                    "Busqueda",
                    "Arbol B+"
            ));
            results.add(runOperation(
                    p -> catalog.getHashTable()
                            .exists(p.getBarcode()),
                    product,
                    "Busqueda",
                    "Tabla Hash"
            ));
            current = current.getNext();
        }
        return results;
    }

    public List<BenchmarkResult> runInsertBenchmark(SimpleProductsList testData, Catalog catalog) {
        List<BenchmarkResult> results = new ArrayList<>(testData.getSize());
        Node<Product> current = testData.getHead();

        while (current != null) {
            Product product = current.getValue();
            results.add(runOperation(
                    p -> catalog.getSimpleList().add(p),
                    product,
                    "Insercion",
                    "Lista Enlazada Secuencial"
            ));
            results.add(runOperation(
                    p -> catalog.getOrderedList().add(p),
                    product,
                    "Insercion",
                    "Lista Enlazada Ordenada"
            ));
            results.add(runOperation(
                    p -> catalog.getAvlTree().insert(p),
                    product,
                    "Insercion",
                    "Arbol AVL"
            ));
            results.add(runOperation(
                    p -> catalog.getbTree().insert(p),
                    product,
                    "Insercion",
                    "Arbol B"
            ));
            results.add(runOperation(
                    p -> catalog.getbPlusTree().insert(p),
                    product,
                    "Insercion",
                    "Arbol B+"
            ));
            results.add(runOperation(
                    p -> {
                        catalog.getHashTable().put(p.getBarcode(), p);
                        return true;
                    },
                    product,
                    "Insercion",
                    "Tabla Hash"
            ));
            current = current.getNext();
        }
        return results;
    }
    
    public List<BenchmarkResult> runDeleteBenchmark(SimpleProductsList testData, Catalog catalog) {
        List<BenchmarkResult> results = new ArrayList<>(testData.getSize());
        Node<Product> current = testData.getHead();

        while (current != null) {
            Product product = current.getValue();
            results.add(runOperation(
                    p -> catalog.getSimpleList()
                            .remove(p),
                    product,
                    "Eliminacion",
                    "Lista Enlazada Secuencial"
            ));
            results.add(runOperation(
                    p -> catalog.getOrderedList()
                            .remove(p),
                    product,
                    "Eliminacion",
                    "Lista Enlazada Ordenada"
            ));
            results.add(runOperation(
                    p -> catalog.getAvlTree()
                            .remove(p),
                    product,
                    "Eliminacion",
                    "Arbol AVL"
            ));
            results.add(runOperation(
                    p -> catalog.getbTree()
                            .remove(p),
                    product,
                    "Eliminacion",
                    "Arbol B"
            ));
            results.add(runOperation(
                    p -> catalog.getbPlusTree()
                            .remove(p),
                    product,
                    "Eliminacion",
                    "Arbol B+"
            ));
            results.add(runOperation(
                    p -> catalog.getHashTable()
                            .remove(p.getBarcode()),
                    product,
                    "Eliminacion",
                    "Tabla Hash"
            ));
            current = current.getNext();
        }
        return results;
    }

    public List<BenchmarkResult> runAllBenchmarks(SimpleProductsList testData, Catalog catalog) {
        List<BenchmarkResult> insert = runInsertBenchmark(testData, catalog);
        List<BenchmarkResult> search = runSearchBenchmark(testData, catalog);
        List<BenchmarkResult> delete = runDeleteBenchmark(testData, catalog);

        return Stream.concat(insert.stream(), Stream.concat(search.stream(), delete.stream())).toList();
    }

    private BenchmarkResult runOperation(Predicate<Product> operation, Product product, String type, String structure) {

        BenchmarkResult result = new BenchmarkResult();

        result.setType(type);
        result.setStructure(structure);

        long start = System.nanoTime();
        result.setSuccess(operation.test(product));
        long end = System.nanoTime();

        result.setTime(end - start);

        return result;
    }
}
