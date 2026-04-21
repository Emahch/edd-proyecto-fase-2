package com.josecarlos.supermarket.model.trees;

import com.josecarlos.supermarket.model.exceptions.OperationException;
import com.josecarlos.supermarket.model.product.Product;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class AVLTree {

    private AVLNode<Product> root;

    public AVLTree() {
    }

    public boolean insert(Product value) {
        try {
            root = insert(root, value);
            return true;
        } catch (OperationException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean remove(Product value) {
        try {
            root = remove(root, value);
            return true;
        } catch (OperationException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Product> searchByName(String name) {
        ArrayList<Product> resultList = new ArrayList<>();
        searchByName(root, name.toLowerCase(), resultList);
        return resultList;
    }

    public void clear() {
        root = clear(root);
    }

    public List<Product> inorder() {
        List<Product> products = new ArrayList<>();
        inorder(root, products);
        return products;
    }

    /*
     * Funciones recursivas
     */
    private AVLNode<Product> insert(AVLNode<Product> node, Product value) throws OperationException {
        if (node == null) {
            return new AVLNode<>(value);
        }

        if (value.compareName(value) < 0) {
            node.setLeft(insert(node.getLeft(), value));
        } else if (value.compare(value) > 0) {
            node.setRight(insert(node.getRight(), value));
        } else {
            throw new OperationException("El producto ya existe en Árbol AVL");
        }

        return balance(node);
    }

    private AVLNode<Product> remove(AVLNode<Product> node, Product value) throws OperationException {
        if (node == null) {
            throw new OperationException("El valor no existe en el árbol AVL");
        }

        if (value.compareName(value) < 0) {
            node.setLeft(remove(node.getLeft(), value));
        } else if (value.compare(value) > 0) {
            node.setRight(remove(node.getRight(), value));
        } else {
            if (node.getLeft() == null && node.getRight() == null) {
                node = null;
                return null;
            } else if (node.getLeft() == null || node.getRight() == null) {
                AVLNode<Product> temp = node.getLeft() != null ? node.getLeft() : node.getRight();
                node = null;
                return temp;
            } else {
                AVLNode<Product> temp = node.getRight();
                while (temp.getLeft() != null) {
                    temp = temp.getLeft();
                }

                node.setValue(temp.getValue());
                node.setRight(remove(node.getRight(), temp.getValue()));
            }
        }
        return balance(node);
    }

    private void searchByName(AVLNode<Product> node, String name, List<Product> resultList) {
        if (node == null) {
            return;
        }

        int result = name.compareTo(node.getValue().getName().toLowerCase());

        if (result < 0) {
            searchByName(node.getLeft(), name, resultList);
        } else if (result > 0) {
            searchByName(node.getRight(), name, resultList);
        } else {
            resultList.add(node.getValue());
            searchByName(node.getLeft(), name, resultList);
            searchByName(node.getRight(), name, resultList);
        }
    }

    private AVLNode<Product> clear(AVLNode<Product> node) {
        if (node == null) {
            return null;
        }

        node.setLeft(clear(node.getLeft()));
        node.setRight(clear(node.getRight()));
        return null;
    }

    private void inorder(AVLNode<Product> node, List<Product> products) {
        if (node == null) {
            return;
        }
        inorder(node.getLeft(), products);
        products.add(node.getValue());
        inorder(node.getRight(), products);
    }

    /*
     * Rotaciones
     */
    private AVLNode<Product> balance(AVLNode<Product> node) {
        node.setHeight(getNewHeight(node));
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.getRight()) >= 0) {
            return rotateRR(node);
        } else if (balance < -1 && getBalance(node.getLeft()) <= 0) {
            return rotateLL(node);
        } else if (balance > 1 && getBalance(node.getRight()) < 0) {
            return rotateRL(node);
        } else if (balance < -1 && getBalance(node.getLeft()) > 0) {
            return rotateLR(node);
        } else {
            return node;
        }
    }

    private AVLNode<Product> rotateRR(AVLNode<Product> node) {
        AVLNode<Product> newRoot = node.getRight();
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);

        node.setHeight(getNewHeight(node));
        newRoot.setHeight(getNewHeight(newRoot));

        return newRoot;
    }

    private AVLNode<Product> rotateLL(AVLNode<Product> node) {
        AVLNode<Product> newRoot = node.getLeft();
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);

        node.setHeight(getNewHeight(node));
        newRoot.setHeight(getNewHeight(newRoot));

        return newRoot;
    }

    private AVLNode<Product> rotateLR(AVLNode<Product> node) {
        AVLNode<Product> leftChild = node.getLeft();
        AVLNode<Product> newRoot = leftChild.getRight();

        leftChild.setRight(newRoot.getLeft());
        newRoot.setLeft(leftChild);
        node.setLeft(newRoot.getRight());
        newRoot.setRight(node);

        node.setHeight(getNewHeight(node));
        leftChild.setHeight(getNewHeight(leftChild));
        newRoot.setHeight(getNewHeight(newRoot));

        return newRoot;
    }

    private AVLNode<Product> rotateRL(AVLNode<Product> node) {
        AVLNode<Product> rightChild = node.getRight();
        AVLNode<Product> newRoot = rightChild.getLeft();

        rightChild.setLeft(newRoot.getRight());
        newRoot.setRight(rightChild);
        node.setRight(newRoot.getLeft());
        newRoot.setLeft(node);

        node.setHeight(getNewHeight(node));
        rightChild.setHeight(getNewHeight(rightChild));
        newRoot.setHeight(getNewHeight(newRoot));

        return newRoot;
    }

    /*
     * Funciones auxiliares 
     */
    private int getNewHeight(AVLNode<Product> node) {
        if (node == null) {
            return 0;
        }
        return getHeight(node.getLeft()) > getHeight(node.getRight()) ? getHeight(node.getLeft()) + 1 : getHeight(node.getRight()) + 1;
    }

    private int getHeight(AVLNode<Product> node) {
        return node == null ? 0 : node.getHeight();
    }

    private int getBalance(AVLNode<Product> node) {
        return node == null ? 0 : getHeight(node.getRight()) - getHeight(node.getLeft());
    }
}
