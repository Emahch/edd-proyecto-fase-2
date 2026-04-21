package com.josecarlos.supermarket.model.trees;

/**
 *
 * @author LENOVO
 * @param <T>
 */
public class BNode<T> {

    private int degree;
    private boolean leaf;
    public int numKeys;
    public T[] keys;
    public BNode<T>[] children;

    public BNode(int degree, boolean leaf) {
        this.degree = degree;
        this.numKeys = 0;
        this.leaf = leaf;
        keys = (T[]) new Object[getMaxKeys()];
        keys = (T[]) new Object[getMaxKeys() + 1];
    }

    public final int getMaxKeys() {
        return 2 * degree + 1;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }
    
    
}
