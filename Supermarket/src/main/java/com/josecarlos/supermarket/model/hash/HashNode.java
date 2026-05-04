package com.josecarlos.supermarket.model.hash;

/**
 *
 * @author LENOVO
 * @param <V>
 */
public class HashNode<V> {

    private String key;
    private V value;
    private HashNode<V> next;

    public HashNode(String key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HashNode<V> getNext() {
        return next;
    }

    public void setNext(HashNode<V> next) {
        this.next = next;
    }

}
