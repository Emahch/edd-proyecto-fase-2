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
public interface ProductsLoaderListener {
    void onProductsLoaded(SimpleProductsList products);
    void onLoadAllProducts();
}
