package com.josecarlos.supermarket.model.lists;

import com.josecarlos.supermarket.model.hash.Almacenable;
import com.josecarlos.supermarket.model.product.Product;
import java.util.Optional;

/**
 *
 * @author LENOVO
 * @param <T>
 */
public class AgenciesList<T extends Almacenable<T>>{

    private DoubleNode<T> head;
    private DoubleNode<T> tail;
    private int size;

    public boolean add(T value) {
        if (getById(value.getKey()).isPresent()) {
            System.out.println("El elemento ya existe en la lista.");
            return false;
        }

        DoubleNode<T> newNode = new DoubleNode<>(value);

        if (head == null) {
            head = newNode;
            tail = newNode;
            size++;
            return true;
        }
        if (value.getKey().compareTo(head.getValue().getKey()) < 0) {
            newNode.setNext(head);
            head.setPrev(newNode);
            head = newNode;
            size++;
            return true;
        } 
        if (value.getKey().compareTo(tail.getValue().getKey()) > 0) {
            tail.setNext(newNode);
            newNode.setPrev(tail);
            tail = newNode;
            size++;
            return true;
        }

        DoubleNode<T> current = head;
        while (current.getNext() != null
                && current.getNext().getValue().getKey().compareTo(value.getKey()) < 0) {
            current = current.getNext();
        }
        
        if (current.getNext() != null && current.getNext().getValue().getKey().compareTo(value.getKey()) == 0) {
            return false;
        }
        
        DoubleNode<T> successor = current.getNext();
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

        DoubleNode<T> target = binarySearch(product.getBarcode());
        if (target == null) {
            System.out.println("El elemento no existe en la lista.");
            return false;
        }

        unlink(target);
        size--;
        return true;
    }

    public Optional<T> getById(String id) {
        DoubleNode<T> node = binarySearch(id);
        return node == null ? Optional.empty() : Optional.of(node.getValue());
    }

    private DoubleNode<T> binarySearch(String target) {
        if (head == null) {
            return null;
        }

        int lo = 0;
        int hi = size - 1;

        DoubleNode<T> loNode = head;
        DoubleNode<T> hiNode = tail;

        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;

            DoubleNode<T> midNode = nodeAt(mid, lo, loNode, hi, hiNode);
            String midBarcode = midNode.getValue().getKey();
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

    private DoubleNode<T> nodeAt(int target,
            int lo, DoubleNode<T> loNode,
            int hi, DoubleNode<T> hiNode) {
        int stepsFromLo = target - lo;
        int stepsFromHi = hi - target;

        if (stepsFromLo <= stepsFromHi) {
            DoubleNode<T> cur = loNode;
            for (int i = 0; i < stepsFromLo; i++) {
                cur = cur.getNext();
            }
            return cur;
        } else {
            DoubleNode<T> cur = hiNode;
            for (int i = 0; i < stepsFromHi; i++) {
                cur = cur.getPrev();
            }
            return cur;
        }
    }

    private void unlink(DoubleNode<T> node) {
        DoubleNode<T> prev = node.getPrev();
        DoubleNode<T> next = node.getNext();

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

    public DoubleNode<T> getHead() {
        return head;
    }

    public DoubleNode<T> getTail() {
        return tail;
    }

    public int getSize() {
        return size;
    }

}
