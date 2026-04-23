package com.josecarlos.supermarket.model.trees;

import com.josecarlos.supermarket.model.exceptions.OperationException;
import com.josecarlos.supermarket.model.lists.SimpleList;
import com.josecarlos.supermarket.model.product.Product;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author LENOVO
 */
public class BTree {

    private BNode<Product> root;
    private final int degree;

    public BTree(int degree) {
        this.root = null;
        this.degree = degree;
    }

    public Product search(Product key) {
        BNode<Product> current = root;

        while (current != null) {
            int i = current.numKeys - 1;
            while (i >= 0 && key.compareDate(current.keys[i]) < 0) {
                i--;
            }
            i++;

            if (i >= 0 && key.compareDate(current.keys[i]) == 0) {
                return current.keys[i];
            }

            if (current.isLeaf()) {
                return null;
            } else {
                current = current.children[i];
            }
        }
        return null;
    }

    public boolean insert(Product key) {
        if (root == null) {
            root = new BNode<>(degree, true);
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
            BNode<Product> newRoot = new BNode<>(degree, false);
            newRoot.children[0] = root;
            splitChild(newRoot, 0);
            root = newRoot;
        }
        return true;

    }

    private void insert(BNode<Product> node, Product key) throws OperationException {
        int i = node.numKeys - 1;

        if (node.isLeaf()) {
            while (i >= 0 && key.compareDate(node.keys[i]) < 0) {
                node.keys[i + 1] = node.keys[i];
                i--;
            }
            if (i >= 0 && key.compareDate(node.keys[i]) == 0) {
                i++;
                while (i < node.numKeys) {
                    node.keys[i] = node.keys[i + 1];
                    i++;
                }
                throw new OperationException("El valor ya existe en arbol B");
            }

            node.keys[i + 1] = key;
            node.numKeys++;
            return;
        }

        while (i >= 0 && key.compareDate(node.keys[i]) < 0) {
            i--;
        }
        i++;

        insert(node.children[i], key);
        if (node.children[i].numKeys == node.getMaxKeys()) {
            splitChild(node, i);
        }
    }

    private void splitChild(BNode<Product> parent, int index) {
        BNode<Product> child = parent.children[index];
        int d = child.getDegree();

        BNode<Product> rightChild = new BNode<>(d, child.isLeaf());
        Product middleKey = child.keys[d];

        rightChild.numKeys = d;
        child.numKeys = d;

        System.arraycopy(child.keys, d + 1, rightChild.keys, 0, d);
        for (int j = d; j <= 2 * d; j++) {
            child.keys[j] = null;
        }

        if (!child.isLeaf()) {
            System.arraycopy(child.children, d + 1, rightChild.children, 0, d + 1);
            for (int j = d + 1; j <= 2 * d + 1; j++) {
                child.children[j] = null;
            }
        }

        if (parent.numKeys > index) {
            System.arraycopy(parent.keys, index, parent.keys, index + 1, parent.numKeys - index);
            System.arraycopy(parent.children, index + 1, parent.children, index + 2, parent.numKeys - index);
        }

        parent.keys[index] = middleKey;
        parent.children[index + 1] = rightChild;
        parent.numKeys++;
    }

    public boolean remove(Product key) {
        if (root == null) {
            return false;
        }

        boolean removed = removeFromSubtree(root, key);

        if (root != null && root.numKeys == 0) {
            if (root.isLeaf()) {
                root = null;
            } else {
                root = root.children[0];
            }
        }

        return removed;
    }

    private boolean removeFromSubtree(BNode<Product> node, Product key) {
        int keyPosition = findKeyPosition(node, key);
        boolean keyExistsInNode = keyPosition < node.numKeys && node.keys[keyPosition].compareDate(key) == 0;

        if (keyExistsInNode) {
            if (node.isLeaf()) {
                return removeFromLeafIfExists(node, key, keyPosition);
            }

            BNode<Product> leftChild = node.children[keyPosition];
            BNode<Product> rightChild = node.children[keyPosition + 1];

            if (leftChild.numKeys > degree) {
                Product predecessor = extractGreatestKey(leftChild);
                node.keys[keyPosition] = predecessor;
                return true;
            }

            if (rightChild.numKeys > degree) {
                Product successor = extractSmallestKey(rightChild);
                node.keys[keyPosition] = successor;
                return true;
            }

            mergeChildren(node, keyPosition);
            return removeFromSubtree(node.children[keyPosition], key);
        }

        if (node.isLeaf()) {
            return false;
        }

        int childIndex = keyPosition;
        BNode<Product> targetChild = node.children[childIndex];

        if (isAtMinimumKeys(targetChild)) {
            if (canBorrowFromLeft(node, childIndex)) {
                borrowFromLeftSibling(node, childIndex);
            } else if (canBorrowFromRight(node, childIndex)) {
                borrowFromRightSibling(node, childIndex);
            } else {
                if (childIndex < node.numKeys) {
                    mergeChildren(node, childIndex);
                } else {
                    mergeChildren(node, childIndex - 1);
                    childIndex--;
                }
            }
            targetChild = node.children[childIndex];
        }

        return removeFromSubtree(targetChild, key);
    }

    private int findKeyPosition(BNode<Product> node, Product key) {
        int i = 0;
        while (i < node.numKeys && node.keys[i].compareDate(key) < 0) {
            i++;
        }
        return i;
    }

    private boolean removeFromLeafIfExists(BNode<Product> leaf, Product key, int keyIndex) {
        if (keyIndex < 0 || keyIndex >= leaf.numKeys || key.compareDate(leaf.keys[keyIndex]) != 0) {
            return false;
        }

        if (leaf.numKeys - 1 - keyIndex > 0) {
            System.arraycopy(leaf.keys, keyIndex + 1, leaf.keys, keyIndex, leaf.numKeys - 1 - keyIndex);
        }
        leaf.numKeys--;
        leaf.keys[leaf.numKeys] = null;
        return true;
    }

    private Product extractGreatestKey(BNode<Product> node) {
        if (node.isLeaf()) {
            int lastIndex = node.numKeys - 1;
            Product greatest = node.keys[lastIndex];
            node.keys[lastIndex] = null;
            node.numKeys--;
            return greatest;
        }

        int rightmostChildIndex = node.numKeys;
        BNode<Product> rightmostChild = node.children[rightmostChildIndex];

        if (isAtMinimumKeys(rightmostChild)) {
            if (canBorrowFromLeft(node, rightmostChildIndex)) {
                borrowFromLeftSibling(node, rightmostChildIndex);
            } else {
                mergeChildren(node, rightmostChildIndex - 1);
                rightmostChild = node.children[rightmostChildIndex - 1];
            }
        }
        return extractGreatestKey(rightmostChild);
    }

    private Product extractSmallestKey(BNode<Product> node) {
        if (node.isLeaf()) {
            Product smallest = node.keys[0];
            System.arraycopy(node.keys, 1, node.keys, 0, node.numKeys - 1);
            node.numKeys--;
            node.keys[node.numKeys] = null;
            return smallest;
        }

        BNode<Product> leftmostChild = node.children[0];
        if (isAtMinimumKeys(leftmostChild)) {
            if (canBorrowFromRight(node, 0)) {
                borrowFromRightSibling(node, 0);
            } else {
                mergeChildren(node, 0);
                leftmostChild = node.children[0];
            }
        }
        return extractSmallestKey(leftmostChild);
    }

    private void borrowFromLeftSibling(BNode<Product> parent, int childIndex) {
        BNode<Product> child = parent.children[childIndex];
        BNode<Product> leftSibling = parent.children[childIndex - 1];

        System.arraycopy(child.keys, 0, child.keys, 1, child.numKeys);
        if (!child.isLeaf()) {
            System.arraycopy(child.children, 0, child.children, 1, child.numKeys + 1);
        }

        child.keys[0] = parent.keys[childIndex - 1];
        parent.keys[childIndex - 1] = leftSibling.keys[leftSibling.numKeys - 1];
        leftSibling.keys[leftSibling.numKeys - 1] = null;

        if (!child.isLeaf()) {
            child.children[0] = leftSibling.children[leftSibling.numKeys];
            leftSibling.children[leftSibling.numKeys] = null;
        }

        leftSibling.numKeys--;
        child.numKeys++;
    }

    private void borrowFromRightSibling(BNode<Product> parent, int childIndex) {
        BNode<Product> child = parent.children[childIndex];
        BNode<Product> rightSibling = parent.children[childIndex + 1];

        child.keys[child.numKeys] = parent.keys[childIndex];
        parent.keys[childIndex] = rightSibling.keys[0];

        System.arraycopy(rightSibling.keys, 1, rightSibling.keys, 0, rightSibling.numKeys - 1);

        if (!child.isLeaf()) {
            child.children[child.numKeys + 1] = rightSibling.children[0];
            System.arraycopy(rightSibling.children, 1, rightSibling.children, 0, rightSibling.numKeys);
            rightSibling.children[rightSibling.numKeys] = null;
        }

        rightSibling.numKeys--;
        rightSibling.keys[rightSibling.numKeys] = null;
        child.numKeys++;
    }

    private void mergeChildren(BNode<Product> parent, int leftChildIndex) {
        BNode<Product> leftChild = parent.children[leftChildIndex];
        BNode<Product> rightChild = parent.children[leftChildIndex + 1];

        int separatorPosition = leftChild.numKeys;
        leftChild.keys[separatorPosition] = parent.keys[leftChildIndex];

        System.arraycopy(rightChild.keys, 0, leftChild.keys, separatorPosition + 1, rightChild.numKeys);

        if (!leftChild.isLeaf()) {
            System.arraycopy(rightChild.children, 0, leftChild.children, separatorPosition + 1, rightChild.numKeys + 1);
        }

        leftChild.numKeys += rightChild.numKeys + 1;

        System.arraycopy(parent.keys, leftChildIndex + 1, parent.keys, leftChildIndex, parent.numKeys - 1 - leftChildIndex);
        System.arraycopy(parent.children, leftChildIndex + 2, parent.children, leftChildIndex + 1, parent.numKeys - 1 - leftChildIndex);

        parent.numKeys--;
        parent.keys[parent.numKeys] = null;
        parent.children[parent.numKeys + 1] = null;
    }

    private boolean canBorrowFromLeft(BNode<Product> parent, int childIndex) {
        return childIndex > 0 && parent.children[childIndex - 1].numKeys > degree;
    }

    private boolean canBorrowFromRight(BNode<Product> parent, int childIndex) {
        return childIndex < parent.numKeys && parent.children[childIndex + 1].numKeys > degree;
    }

    private boolean isAtMinimumKeys(BNode<Product> node) {
        return node.numKeys == degree;
    }

    public SimpleList searchByExpiryDateRange(String startDate, String endDate) {
        SimpleList resultList = new SimpleList();
        if (root != null) {
            searchByExpiryDateRange(root, startDate, endDate, resultList);
        }
        return resultList;
    }

    private void searchByExpiryDateRange(BNode<Product> node, String startDate, String endDate, SimpleList resultList) {
        if (node == null) {
            return;
        }

        String startDateLower = startDate.toLowerCase();
        String endDateLower = endDate.toLowerCase();

        int i = 0;
        while (i < node.numKeys) {
            String keyDateLower = node.keys[i].getExpireDate().toLowerCase();

            if (keyDateLower.compareTo(startDateLower) >= 0 && keyDateLower.compareTo(endDateLower) <= 0) {
                resultList.add(node.keys[i]);
            }

            if (keyDateLower.compareTo(startDateLower) < 0) {
                i++;
            } else {
                if (!node.isLeaf()) {
                    searchByExpiryDateRange(node.children[i], startDate, endDate, resultList);
                }
                if (keyDateLower.compareTo(endDateLower) > 0) {
                    break;
                }
                i++;
            }
        }

        if (!node.isLeaf() && i <= node.numKeys) {
            if (i == node.numKeys) {
                String lastKeyDateLower = node.keys[node.numKeys - 1].getExpireDate().toLowerCase();
                if (lastKeyDateLower.compareTo(endDateLower) < 0) {
                    searchByExpiryDateRange(node.children[i], startDate, endDate, resultList);
                }
            }
        }
    }

    public void generateDotFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("digraph BTree {");
            writer.println("graph [ranksep=0.8, nodesep=0.35, splines=false, overlap=false];");
            writer.println("node [shape=record, style=\"rounded,filled\", fillcolor=\"#FFF9F0\", color=\"#A44A3F\", fontname=\"Helvetica\", fontsize=9, margin=\"0.08,0.04\"];");
            writer.println("edge [color=\"#7A6C5D\", penwidth=1.0, tailclip=false, arrowtail=none];\n");

            if (root != null) {
                int[] nodeId = {0};
                generateDot(root, writer, nodeId, -1);
            }
            writer.println("}");

            generateImageFiles(filename);

        } catch (IOException e) {
            System.out.println("Error al generar el archivo DOT: " + e.getMessage());
        }
    }

    private void generateDot(BNode<Product> node, PrintWriter writer, int[] nodeId, int parentId) {
        if (node == null) {
            return;
        }

        int currentNodeId = nodeId[0];
        nodeId[0]++;

        StringBuilder label = new StringBuilder();
        for (int i = 0; i < node.numKeys; i++) {
            if (i > 0) {
                label.append("|");
            }
            label.append(node.keys[i].getExpireDate()).append("\\n")
                    .append(node.keys[i].getBarcode()).append("\\n")
                    .append(node.keys[i].getName());
        }

        writer.printf("node%d [label=\"%s\"];\n", currentNodeId, label.toString());

        if (parentId != -1) {
            writer.printf("node%d -> node%d;\n", parentId, currentNodeId);
        }

        if (!node.isLeaf()) {
            for (int i = 0; i <= node.numKeys; i++) {
                if (node.children[i] != null) {
                    generateDot(node.children[i], writer, nodeId, currentNodeId);
                }
            }
        }
    }

    private void generateImageFiles(String filename) {
        try {
            String baseFilename = filename.substring(0, filename.lastIndexOf('.'));
            String svgFilename = baseFilename + ".svg";
            String pngFilename = baseFilename + ".png";

            Process svgProcess = new ProcessBuilder("dot", "-Tsvg", filename, "-o", svgFilename).start();
            Process pngProcess = new ProcessBuilder("dot", "-Tpng", "-Gdpi=220", filename, "-o", pngFilename).start();

            svgProcess.waitFor();
            pngProcess.waitFor();

            System.out.println("SVG generado (recomendado para muchos nodos): " + svgFilename);
            System.out.println("PNG generado: " + pngFilename);

        } catch (IOException | InterruptedException e) {
            System.out.println("Aviso: No se pudieron generar los SVG/PNG automáticos. Verifica que 'Graphviz' esté instalado en tu sistema. " + e.getMessage());
        }
    }

}
