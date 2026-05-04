package com.josecarlos.supermarket.model.hash;

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
}
