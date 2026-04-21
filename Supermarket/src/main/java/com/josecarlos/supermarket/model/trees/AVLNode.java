package com.josecarlos.supermarket.model.trees;

/**
 *
 * @author LENOVO
 * @param <T>
 */
public class AVLNode<T> {
    private T value;
    private AVLNode<T> left;
    private AVLNode<T> right;
    private int height;

    public AVLNode(T value) {
        this.value = value;
        this.height = 0;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public AVLNode<T> getLeft() {
        return left;
    }

    public void setLeft(AVLNode<T> left) {
        this.left = left;
    }

    public AVLNode<T> getRight() {
        return right;
    }

    public void setRight(AVLNode<T> right) {
        this.right = right;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
