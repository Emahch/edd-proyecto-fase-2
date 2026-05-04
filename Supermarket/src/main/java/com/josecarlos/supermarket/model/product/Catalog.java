package com.josecarlos.supermarket.model.product;

import com.josecarlos.supermarket.model.hash.HashTable;
import com.josecarlos.supermarket.model.lists.Comparator;
import com.josecarlos.supermarket.model.lists.Node;
import com.josecarlos.supermarket.model.lists.OrderedList;
import com.josecarlos.supermarket.model.lists.SimpleProductsList;
import com.josecarlos.supermarket.model.trees.AVLTree;
import com.josecarlos.supermarket.model.trees.BPlusTree;
import com.josecarlos.supermarket.model.trees.BTree;
import com.josecarlos.supermarket.view.listeners.ProductListener;
import java.util.Optional;

/**
 *
 * @author LENOVO
 */
public class Catalog implements ProductListener {

    private SimpleProductsList simpleList;
    private OrderedList orderedList;
    private AVLTree avlTree;
    private BTree bTree;
    private BPlusTree bPlusTree;
    private HashTable<Product> hashTable;

    public Catalog() {
        this.simpleList = new SimpleProductsList();
        this.orderedList = new OrderedList();
        this.avlTree = new AVLTree();
        this.bTree = new BTree(3);
        this.bPlusTree = new BPlusTree(3);
        this.hashTable = new HashTable(1500);
    }

    private boolean addProduct(Product product) {
        if (hashTable.exists(product.getKey())) {
            return false;
        }
        if (!simpleList.add(product) || !orderedList.add(product) || !bTree.insert(product) || !bPlusTree.insert(product) || !avlTree.insert(product)) {
            removeProduct(product);
            return false;
        }
        hashTable.put(product.getKey(), product);
        return true;
    }

    private void removeProduct(Product product) {
        simpleList.remove(product);
        orderedList.remove(product);
        avlTree.remove(product);
        bTree.remove(product);
        bPlusTree.remove(product);
        hashTable.remove(product.getKey());
    }

    @Override
    public boolean onProductCreated(Product product) {
        return addProduct(product);
    }

    @Override
    public boolean onProductModified(Product product) {
        removeProduct(product);
        return addProduct(product);
    }

    @Override
    public void onProductDeleted(Product product) {
        removeProduct(product);
    }

    @Override
    public SimpleProductsList searchProduct(String key) {
        Optional<Product> posibleProduct = hashTable.get(key);
        if (posibleProduct.isEmpty()) {
            return searchByCoincidence(
                    (product, atribute) -> product.getBarcode().toLowerCase()
                            .contains(atribute.toLowerCase()), key);
        }
        SimpleProductsList products = new SimpleProductsList();
        products.add(posibleProduct.get());
        return products;
    }

    @Override
    public SimpleProductsList searchByCategory(String category) {
        SimpleProductsList result = bPlusTree.searchByCategory(category);
        if (result.isEmpty()) {
            return searchByCoincidence(
                    (product, atribute) -> product.getCategory().toLowerCase()
                            .contains(atribute.toLowerCase()), category);
        }
        return result;
    }

    @Override
    public SimpleProductsList searchByName(String name) {
        SimpleProductsList result = avlTree.searchByName(name);
        if (result.isEmpty()) {
            return searchByCoincidence(
                    (product, atribute) -> product.getName().toLowerCase()
                            .contains(atribute.toLowerCase()), name);
        }
        return result;
    }

    @Override
    public SimpleProductsList searchByDate(String startDate, String endDate) {
        SimpleProductsList result = bTree.searchByExpiryDateRange(startDate, endDate);
        if (result.isEmpty()) {
            return searchByCoincidence(
                    (product, atribute) -> product.getExpireDate().toLowerCase()
                            .contains(atribute.toLowerCase()), startDate);
        }
        return result;
    }

    private SimpleProductsList searchByCoincidence(Comparator comparator, String atribute) {
        SimpleProductsList coincidences = new SimpleProductsList();
        Node<Product> head = simpleList.getHead();
        while (head != null) {
            if (comparator.contains(head.getValue(), atribute)) {
                coincidences.add(head.getValue());
            }
            head = head.getNext();
        }
        return coincidences;
    }
    
    public SimpleProductsList inorder() {
        return avlTree.inorder();
    }

}
