package com.josecarlos.supermarket.model.hash;

import java.util.Optional;

/**
 *
 * @author LENOVO
 * @param <K>
 * @param <V>
 */
public class HashTable<K, V> {

    private int size;
    private HashNode<K, V>[] values;
    private final int capacity;

    public HashTable(int wishedCapacity) {
        this.capacity = getNextPrime(wishedCapacity);
        size = 0;
        values = (HashNode<K, V>[]) new Object[this.capacity];
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

    private int getIndex(K key) {
        int hashCode = key.hashCode();
        int index = hashCode % capacity;

        if (index >= capacity) {
            return Math.abs(index);
        }
        return Math.abs(index);
    }

    public void put(K key, V value) {
        int index = getIndex(key);
        HashNode<K, V> head = values[index];

        while (head != null) {
            if (head.getKey().equals(key)) {
                head.setValue(value);
                return;
            }
            head = head.getNext();
        }

        size++;
        head = values[index];
        HashNode<K, V> newNode = new HashNode<>(key, value);
        newNode.setNext(head);
        values[index] = newNode;
    }

    public Optional<V> get(K key) {
        int index = getIndex(key);
        HashNode<K, V> head = values[index];

        while (head != null) {
            if (head.getKey().equals(key)) {
                return Optional.of(head.getValue());
            }
            head = head.getNext();
        }

        return Optional.empty();
    }

    public boolean remove(K key) {
        int index = getIndex(key);
        HashNode<K, V> head = values[index];
        HashNode<K, V> prev = null;

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

}
