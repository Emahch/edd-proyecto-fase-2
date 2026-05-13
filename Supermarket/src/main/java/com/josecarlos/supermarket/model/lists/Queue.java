package com.josecarlos.supermarket.model.lists;

/**
 *
 * @author LENOVO
 */
public class Queue<T> {
    private Node<T> first;
    private Node<T> last;
    private int size;

    public Queue() {
        this.first = null;
        this.last = null;
        this.size = 0;
    }

    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        if (isEmpty()) {
            first = newNode;
            last = newNode;
        } else {
            last.setNext(newNode);
            last = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            System.out.println("La cola esta vacia");
            return null;
        }
        T value = first.getValue();
        first = first.getNext();
        size--;

        if (isEmpty()) {
            last = null;
        }
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            System.out.println("La cola esta vacia");
            return null;
        }
        return first.getValue();
    }

    public boolean isEmpty() {
        return first == null;
    }

    public int getSize() {
        return size;
    }
    
}
