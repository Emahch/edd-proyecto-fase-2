package com.josecarlos.supermarket.model.lists;

import com.josecarlos.supermarket.model.product.Product;
import java.util.Optional;

/**
 *
 * @author LENOVO
 */
public class SimpleProductsList {

    protected Node<Product> head;
    protected Node<Product> tail;
    protected int size;

    public boolean add(Product value) {
        Node<Product> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
        }

        size++;
        return true;
    }

    public boolean remove(Product product) {
        Node<Product> current = head;
        Node<Product> previous = null;

        while (current != null && current.getValue().compareTo(product) != 0) {
            previous = current;
            current = current.getNext();
        }

        if (current == null) {
            System.out.println("El producto no existe en la lista");
            return false;
        }

        if (previous == null) {
            head = current.getNext();
        } else {
            previous.setNext(current.getNext());
        }
        size--;
        return true;
    }

    public Optional<Product> getByBarcode(String barcode) {
        Node<Product> current = head;

        while (current != null && !current.getValue().getBarcode().equals(barcode)) {
            current = current.getNext();
        }

        if (current == null) {
            return Optional.empty();
        }
        return Optional.of(current.getValue());
    }

    public Node<Product> getHead() {
        return head;
    }

    public Product getByIndex(int index) {
        if (index < 0) {
            return null;
        }

        Node<Product> current = head;
        int count = 0;

        while (current != null) {
            if (count == index) {
                return current.getValue();
            }
            current = current.getNext();
            count++;
        }
        return null;
    }

    public Node<Product> getTail() {
        return tail;
    }

    public int getSize() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

}
