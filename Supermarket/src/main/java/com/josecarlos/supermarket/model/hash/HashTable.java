package com.josecarlos.supermarket.model.hash;

import com.josecarlos.supermarket.model.product.Product;
import com.josecarlos.supermarket.services.ExporterSVG;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author LENOVO
 * @param <V>
 */
public class HashTable<V> {

    private int size;
    private HashNode<V>[] values;
    private final int capacity;

    public HashTable(int wishedCapacity) {
        this.capacity = getNextPrime(wishedCapacity);
        size = 0;
        values = (HashNode<V>[]) new HashNode[this.capacity];
    }

    private int getNextPrime(int n) {
        int prime = n;
        while (!isPrime(prime)) {
            prime++;
        }
        return prime;
    }

    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }

        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }

        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private int getIndex(String key) {
        int index = key.hashCode();

        index = Math.abs(index);
        while (index >= capacity) {
            index = index % capacity;
        }
        return index;
    }

    public void put(String key, V value) {
        int index = getIndex(key);
        HashNode<V> head = values[index];

        while (head != null) {
            if (head.getKey().compareTo(key) == 0) {
                head.setValue(value);
                return;
            }
            head = head.getNext();
        }

        size++;
        head = values[index];
        HashNode<V> newNode = new HashNode<>(key, value);
        newNode.setNext(head);
        values[index] = newNode;
    }

    public Optional<V> get(String key) {
        int index = getIndex(key);
        HashNode<V> head = values[index];

        while (head != null) {
            if (head.getKey().compareTo(key) == 0) {
                return Optional.of(head.getValue());
            }
            head = head.getNext();
        }

        return Optional.empty();
    }

    public boolean exists(String key) {
        int index = getIndex(key);
        HashNode<V> head = values[index];

        while (head != null) {
            if (head.getKey().compareTo(key) == 0) {
                return true;
            }
            head = head.getNext();
        }

        return false;
    }

    public boolean remove(String key) {
        int index = getIndex(key);
        HashNode<V> head = values[index];
        HashNode<V> prev = null;

        while (head != null) {
            if (head.getKey().equals(key)) {
                break;
            }
            prev = head;
            head = head.getNext();
        }

        if (head == null) {
            return false;
        }

        size--;

        if (prev == null) {
            values[index] = head.getNext();
        } else {
            prev.setNext(head.getNext());
        }

        return true;
    }

    public List<HashNode<V>> getValues() {
        List<HashNode<V>> val = new ArrayList();

        for (HashNode<V> value : values) {
            while (value != null) {
                val.add(value);
                value = value.getNext();
            }
        }
        return val;
    }

    public void generateGraphviz(String folderPath) {
        String file = folderPath + File.separator + "hash";

        try (PrintWriter pw = new PrintWriter(new FileWriter(file.concat(".dot")))) {
            pw.println("digraph HashTable {");
            pw.println("    rankdir=LR;");
            pw.println("    node [shape=record, fontname=\"Arial\", style=filled];");

            StringBuilder bucketsLabel = new StringBuilder();
            bucketsLabel.append("buckets [fillcolor=\"#EBF5FB\", label=\"");

            boolean first = true;
            for (int i = 0; i < capacity; i++) {
                if (values[i] != null) {
                    if (!first) {
                        bucketsLabel.append("|");
                    }
                    bucketsLabel.append("<f").append(i).append("> Indice: ").append(i);
                    first = false;
                }
            }
            bucketsLabel.append("\"];");

            pw.println("    " + bucketsLabel.toString());

            for (int i = 0; i < capacity; i++) {
                HashNode<V> current = values[i];
                if (current != null) {
                    String lastID = "buckets:f" + i;

                    while (current != null) {
                        String currentID = "node" + System.identityHashCode(current);

                        String keyStr = current.getKey().replace("\"", "\\\"");
                        String valStr = String.valueOf(current.getValue()).replace("\"", "\\\"");
                        if (current.getValue() instanceof Product product) {
                            valStr = product.getName();
                        }
                        if (valStr.length() > 50) {
                            valStr = valStr.substring(0, 47) + "...";
                        }

                        pw.println("    " + currentID + " [fillcolor=\"#AED6F1\", label=\"{Key: " + keyStr + " | Val: " + valStr + "}\"];");
                        pw.println("    " + lastID + " -> " + currentID + " [color=\"#1B4F72\"];");

                        lastID = currentID;
                        current = current.getNext();
                    }
                }
            }

            pw.println("}");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }

        ExporterSVG.exportToSvg(file);
    }
}
