/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.josecarlos.supermarket.view.listeners;

import com.josecarlos.supermarket.model.lists.SimpleProductsList;
import com.josecarlos.supermarket.model.product.Product;
import java.util.Optional;

/**
 *
 * @author LENOVO
 */
public interface ProductListener {
    boolean onProductCreated(Product product);
    boolean onProductModified(Product product);
    void onProductDeleted(Product product);
    SimpleProductsList searchProduct(String key);
    SimpleProductsList searchByCategory(String category);
    SimpleProductsList searchByName(String name);
    SimpleProductsList searchByDate(String startDate, String endDate);
}
