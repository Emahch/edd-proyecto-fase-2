package com.josecarlos.supermarket.model.lists;

import com.josecarlos.supermarket.model.product.Product;
import java.util.Optional;

/**
 *
 * @author LENOVO
 */
public class OrderedList {

    private DoubleNode<Product> head;
    private DoubleNode<Product> tail;
    private int size;

    public boolean add(Product value) {
        if (getByBarcode(value.getBarcode()).isPresent()) {
            System.out.println("El producto con código '" + value.getBarcode() + "' ya existe en la lista.");
            return false;
        }

        DoubleNode<Product> newNode = new DoubleNode<>(value);

        if (head == null) {
            head = newNode;
            tail = newNode;
            size++;
            return true;
        }
        if (value.getBarcode().compareTo(head.getValue().getBarcode()) < 0) {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
            size++;
            return true;
        } 
        if (value.getBarcode().compareTo(tail.getValue().getBarcode()) > 0) {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
            size++;
            return true;
        }

        DoubleNode<Product> current = head;
        while (current.getNext() != null
                && current.getNext().getValue().getBarcode().compareTo(value.getBarcode()) < 0) {
            current = current.getNext();
        }
        
        if (current.getNext() != null && current.getNext().getValue().getBarcode().compareTo(value.getBarcode()) == 0) {
            return false;
        }
        
        DoubleNode<Product> successor = current.getNext();
        newNode.setPrev(current);
        newNode.setNext(successor);
        current.setNext(newNode);
        if (successor != null) {
            successor.setPrev(newNode);
        }
        size++;
        return true;
    }

    public boolean remove(Product product) {
        if (head == null) {
            System.out.println("La lista está vacía.");
            return false;
        }

        DoubleNode<Product> target = binarySearch(product.getBarcode());
        if (target == null) {
            System.out.println("El producto con código '" + product.getBarcode() + "' no existe en la lista.");
            return false;
        }

        unlink(target);
        size--;
        return true;
    }

    public Optional<Product> getByBarcode(String barcode) {
        DoubleNode<Product> node = binarySearch(barcode);
        return node == null ? Optional.empty() : Optional.of(node.getValue());
    }

    private DoubleNode<Product> binarySearch(String target) {
        if (head == null) {
            return null;
        }

        int lo = 0;
        int hi = size - 1;

        DoubleNode<Product> loNode = head;
        DoubleNode<Product> hiNode = tail;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;

            DoubleNode<Product> midNode = nodeAt(mid, lo, loNode, hi, hiNode);
            String midBarcode = midNode.getValue().getBarcode();
            int cmp = target.compareTo(midBarcode);

            if (cmp == 0) {
                return midNode;
            }

            if (cmp < 0) {
                hi = mid - 1;
                hiNode = midNode.getPrev();
            } else {
                lo = mid + 1;
                loNode = midNode.getNext();
            }
        }
        return null;
    }

    private DoubleNode<Product> nodeAt(int target,
            int lo, DoubleNode<Product> loNode,
            int hi, DoubleNode<Product> hiNode) {
        int stepsFromLo = target - lo;
        int stepsFromHi = hi - target;

        if (stepsFromLo <= stepsFromHi) {
            DoubleNode<Product> cur = loNode;
            for (int i = 0; i < stepsFromLo; i++) {
                cur = cur.getNext();
            }
            return cur;
        } else {
            DoubleNode<Product> cur = hiNode;
            for (int i = 0; i < stepsFromHi; i++) {
                cur = cur.getPrev();
            }
            return cur;
        }
    }

    private void unlink(DoubleNode<Product> node) {
        DoubleNode<Product> prev = node.getPrev();
        DoubleNode<Product> next = node.getNext();

        if (prev != null) {
            prev.setNext(next);
        } else {
            head = next;
        }
        if (next != null) {
            next.setPrev(prev);
        } else {
            tail = prev;
        }
        node.setNext(null);
        node.setPrev(null);
    }

    public DoubleNode<Product> getHead() {
        return head;
    }

    public DoubleNode<Product> getTail() {
        return tail;
    }

    public int getSize() {
        return size;
    }
}
