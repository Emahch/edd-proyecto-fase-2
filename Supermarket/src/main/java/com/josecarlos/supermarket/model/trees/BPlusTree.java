package com.josecarlos.supermarket.model.trees;

import com.josecarlos.supermarket.model.exceptions.OperationException;
import com.josecarlos.supermarket.model.lists.SimpleProductsList;
import com.josecarlos.supermarket.model.product.Product;
import com.josecarlos.supermarket.services.ExporterSVG;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BPlusTree {

    private BNode root;
    private final int degree;

    public BPlusTree(int degree) {
        this.root = null;
        this.degree = degree;
    }

    public boolean insert(Product key) {
        if (root == null) {
            root = new BNode(degree, true);
            root.keys[0] = key;
            root.numKeys = 1;
            return true;
        }

        try {
            insert(root, key);
        } catch (OperationException e) {
            System.out.println(e.getMessage());
            return false;
        }

        if (root.numKeys == root.getMaxKeys()) {
            BNode newRoot = new BNode(degree, false);
            newRoot.children[0] = root;
            splitChild(newRoot, 0, root);
            root = newRoot;
        }
        return true;
    }

    private void insert(BNode node, Product key) throws OperationException {
        if (node.isLeaf()) {
            int i = node.numKeys - 1;
            while (i >= 0 && key.compareCategory(node.keys[i]) < 0) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            if (i >= 0 && key.compareCategory(node.keys[i]) == 0) {
                for (int j = i + 1; j < node.numKeys; j++) {
                    node.keys[j] = node.keys[j + 1];
                }
                throw new OperationException("El producto ya existe en el árbol B+");
            }
            node.keys[i + 1] = key;
            node.numKeys++;

        } else {
            int i = node.numKeys - 1;
            while (i >= 0 && key.compareCategory(node.keys[i]) < 0) {
                i--;
            }
            i++;

            insert(node.children[i], key);
            if (node.children[i].numKeys == node.children[i].getMaxKeys()) {
                splitChild(node, i, node.children[i]);
            }
        }
    }

    private void splitChild(BNode parent, int childIndex, BNode fullChild) {
        int d = degree;
        BNode newChild = new BNode(degree, fullChild.isLeaf());

        for (int j = parent.numKeys; j > childIndex; j--) {
            parent.keys[j] = parent.keys[j - 1];
        }
        for (int j = parent.numKeys + 1; j > childIndex + 1; j--) {
            parent.children[j] = parent.children[j - 1];
        }
        parent.children[childIndex + 1] = newChild;
        parent.numKeys++;

        if (fullChild.isLeaf()) {
            newChild.numKeys = fullChild.numKeys - d;
            for (int j = 0; j < newChild.numKeys; j++) {
                newChild.keys[j] = fullChild.keys[d + j];
            }
            fullChild.numKeys = d;
            parent.keys[childIndex] = newChild.keys[0];

        } else {
            newChild.numKeys = fullChild.numKeys - d - 1;
            for (int j = 0; j < newChild.numKeys; j++) {
                newChild.keys[j] = fullChild.keys[d + 1 + j];
            }
            for (int j = 0; j <= newChild.numKeys; j++) {
                newChild.children[j] = fullChild.children[d + 1 + j];
            }
            parent.keys[childIndex] = fullChild.keys[d];
            fullChild.numKeys = d;
        }
    }

    public boolean remove(Product key) {
        if (root == null || root.numKeys == 0) {
            return false;
        }

        boolean removed = remove(root, key);

        if (root.numKeys == 0) {
            root = root.isLeaf() ? null : root.children[0];
        }
        return removed;
    }

    private boolean remove(BNode node, Product key) {
        int i = findKeyIndex(node, key);

        if (node.isLeaf()) {
            if (i < node.numKeys && key.compareCategory(node.keys[i]) == 0) {
                shiftLeft(node, i);
                return true;
            }
            return false;
        }

        boolean keyHere = (i < node.numKeys && key.compareCategory(node.keys[i]) == 0);

        if (keyHere) {
            return removeFromInternal(node, i);
        } else {
            boolean removed = remove(node.children[i], key);
            if (node.children[i].numKeys < degree) {
                fillChild(node, i);
            }
            return removed;
        }
    }

    private boolean removeFromInternal(BNode node, int i) {
        Product key = node.keys[i];

        if (node.children[i].numKeys > degree) {
            Product pred = getRightmostLeafKey(node.children[i]);
            node.keys[i] = pred;
            boolean removed = remove(node.children[i], pred);
            if (node.children[i].numKeys < degree) {
                fillChild(node, i);
            }
            return removed;

        } else if (node.children[i + 1].numKeys > degree) {
            Product succ = getLeftmostLeafKey(node.children[i + 1]);
            node.keys[i] = succ;
            boolean removed = remove(node.children[i + 1], succ);
            if (node.children[i + 1].numKeys < degree) {
                fillChild(node, i + 1);
            }
            return removed;

        } else {
            mergeChildren(node, i);
            boolean removed = remove(node.children[i], key);
            if (node.children[i].numKeys < degree) {
                fillChild(node, i);
            }
            return removed;
        }
    }

    private void shiftLeft(BNode node, int i) {
        for (int j = i + 1; j < node.numKeys; j++) {
            node.keys[j - 1] = node.keys[j];
        }
        node.keys[--node.numKeys] = null;
    }

    private int findKeyIndex(BNode node, Product key) {
        int i = 0;
        while (i < node.numKeys && key.compareCategory(node.keys[i]) > 0) {
            i++;
        }
        return i;
    }

    private Product getRightmostLeafKey(BNode node) {
        BNode cur = node;
        while (!cur.isLeaf()) {
            cur = cur.children[cur.numKeys];
        }
        return cur.keys[cur.numKeys - 1];
    }

    private Product getLeftmostLeafKey(BNode node) {
        BNode cur = node;
        while (!cur.isLeaf()) {
            cur = cur.children[0];
        }
        return cur.keys[0];
    }

    private void fillChild(BNode parent, int i) {
        if (i > 0 && parent.children[i - 1].numKeys > degree) {
            borrowFromPrev(parent, i);
        } else if (i < parent.numKeys && parent.children[i + 1].numKeys > degree) {
            borrowFromNext(parent, i);
        } else if (i < parent.numKeys) {
            mergeChildren(parent, i);
        } else {
            mergeChildren(parent, i - 1);
        }
    }

    private void borrowFromPrev(BNode parent, int i) {
        BNode child = parent.children[i];
        BNode sibling = parent.children[i - 1];

        for (int j = child.numKeys - 1; j >= 0; j--) {
            child.keys[j + 1] = child.keys[j];
        }
        if (!child.isLeaf()) {
            for (int j = child.numKeys; j >= 0; j--) {
                child.children[j + 1] = child.children[j];
            }
        }

        if (child.isLeaf()) {
            child.keys[0] = sibling.keys[sibling.numKeys - 1];
            parent.keys[i - 1] = child.keys[0];
        } else {
            child.keys[0] = parent.keys[i - 1];
            parent.keys[i - 1] = sibling.keys[sibling.numKeys - 1];
            child.children[0] = sibling.children[sibling.numKeys];
        }

        sibling.keys[--sibling.numKeys] = null;
        child.numKeys++;
    }

    private void borrowFromNext(BNode parent, int i) {
        BNode child = parent.children[i];
        BNode sibling = parent.children[i + 1];

        if (child.isLeaf()) {
            child.keys[child.numKeys] = sibling.keys[0];
            for (int j = 0; j < sibling.numKeys - 1; j++) {
                sibling.keys[j] = sibling.keys[j + 1];
            }
            sibling.keys[sibling.numKeys - 1] = null;
            sibling.numKeys--;
            parent.keys[i] = sibling.keys[0];
        } else {
            child.keys[child.numKeys] = parent.keys[i];
            child.children[child.numKeys + 1] = sibling.children[0];
            parent.keys[i] = sibling.keys[0];
            for (int j = 1; j < sibling.numKeys; j++) {
                sibling.keys[j - 1] = sibling.keys[j];
            }
            for (int j = 1; j <= sibling.numKeys; j++) {
                sibling.children[j - 1] = sibling.children[j];
            }
            sibling.keys[sibling.numKeys - 1] = null;
            sibling.numKeys--;
        }
        child.numKeys++;
    }

    private void mergeChildren(BNode parent, int i) {
        BNode left = parent.children[i];
        BNode right = parent.children[i + 1];

        if (!left.isLeaf()) {
            left.keys[left.numKeys++] = parent.keys[i];
        }

        System.arraycopy(right.keys, 0, left.keys, left.numKeys, right.numKeys);
        if (!left.isLeaf()) {
            for (int j = 0; j <= right.numKeys; j++) {
                left.children[left.numKeys + j] = right.children[j];
            }
        }
        left.numKeys += right.numKeys;

        for (int j = i + 1; j < parent.numKeys; j++) {
            parent.keys[j - 1] = parent.keys[j];
        }
        for (int j = i + 2; j <= parent.numKeys; j++) {
            parent.children[j - 1] = parent.children[j];
        }
        parent.keys[parent.numKeys - 1] = null;
        parent.children[parent.numKeys] = null;
        parent.numKeys--;
    }

    public SimpleProductsList search(Product key) {
        SimpleProductsList result = new SimpleProductsList();
        if (root != null) {
            searchNode(root, key, result);
        }
        return result;
    }

    private void searchNode(BNode node, Product key, SimpleProductsList result) {
        int i = findKeyIndex(node, key);

        if (node.isLeaf()) {
            while (i < node.numKeys && node.keys[i].compareCategory(key) == 0) {
                result.add(node.keys[i++]);
            }
        } else {
            if (i < node.numKeys && node.keys[i].compareCategory(key) == 0) {
                searchNode(node.children[i + 1], key, result);
            } else {
                searchNode(node.children[i], key, result);
            }
        }
    }

    public SimpleProductsList searchByCategory(String category) {
        SimpleProductsList result = new SimpleProductsList();
        if (root != null) {
            searchByCategory(root, category, result);
        }
        return result;
    }

    private void searchByCategory(BNode node, String category, SimpleProductsList result) {
        if (node.isLeaf()) {
            for (int i = 0; i < node.numKeys; i++) {
                if (node.keys[i].getCategory().equalsIgnoreCase(category)) {
                    result.add(node.keys[i]);
                }
            }
        } else {
            for (int i = 0; i <= node.numKeys; i++) {
                boolean leftOk = (i == 0)
                        || node.keys[i - 1].getCategory().compareToIgnoreCase(category) <= 0;
                boolean rightOk = (i == node.numKeys)
                        || node.keys[i].getCategory().compareToIgnoreCase(category) >= 0;
                if (leftOk && rightOk) {
                    searchByCategory(node.children[i], category, result);
                }
            }
        }
    }

    public void generateGraphviz(String fileName) {
        String file = fileName + File.separator + "bPlus";
        try (PrintWriter pw = new PrintWriter(new FileWriter(file + ".dot"))) {
            pw.println("digraph BTree {");
            pw.println("    node [shape=record, height=.1];");

            if (root != null) {
                generateGraphvizRecursive(root, pw);
            } else {
                pw.println("    empty [label=\"Árbol Vacío\"];");
            }

            pw.println("}");
            System.out.println("Archivo Graphviz (Árbol B+) generado: " + file);
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo: " + e.getMessage());
        }
        ExporterSVG.exportToSvg(file);
    }

    private void generateGraphvizRecursive(BNode node, PrintWriter pw) {
        if (node == null) {
            return;
        }

        String nodeID = "node" + System.identityHashCode(node);

        StringBuilder label = new StringBuilder();
        label.append("<p0>");
        for (int i = 0; i < node.numKeys; i++) {
            String keyLabel = node.keys[i].getCategory().replace("\"", "\\\"");
            label.append(" | ").append(keyLabel).append("--").append(node.keys[i].getBarcode()).append(" | <p").append(i + 1).append(">");
        }

        pw.println("    " + nodeID + " [label=\"" + label.toString() + "\"];");

        if (!node.isLeaf()) {
            for (int i = 0; i <= node.numKeys; i++) {
                if (node.children[i] != null) {
                    String childID = "node" + System.identityHashCode(node.children[i]);
                    pw.println("    " + nodeID + ":p" + i + " -> " + childID + ";");
                    generateGraphvizRecursive(node.children[i], pw);
                }
            }
        }
    }

}
