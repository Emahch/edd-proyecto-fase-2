package com.josecarlos.supermarket.model.hash;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author LENOVO
 * @param <K>
 * @param <V>
 */
public class HashTable<K extends Almacenable, V> {

    private int size;
    private HashNode<K, V>[] values;
    private final int capacity;

    public HashTable(int wishedCapacity) {
        this.capacity = getNextPrime(wishedCapacity);
        size = 0;
        values = (HashNode<K, V>[]) new HashNode[this.capacity];
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
        String hashCode = key.getKey();
        int index = hashCode.hashCode();

        index = Math.abs(index);
        while (index >= capacity) {
            index = index % capacity;
        }
        return index;
    }

    public void put(K key, V value) {
        int index = getIndex(key);
        HashNode<K, V> head = values[index];

        while (head != null) {
            if (head.getKey().compareTo(key) == 0) {
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
            if (head.getKey().compareTo(key) == 0) {
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

    public List<HashNode<K, V>> getValues() {
        List<HashNode<K, V>> val = new ArrayList();

        for (HashNode<K, V> value : values) {
            while (value != null) {
                val.add(value);
                value = value.getNext();
            }
        }
        return val;
    }
}
