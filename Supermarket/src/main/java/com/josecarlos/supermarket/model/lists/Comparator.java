package com.josecarlos.supermarket.model.lists;

import com.josecarlos.supermarket.model.product.Product;

/**
 *
 * @author LENOVO
 */
@FunctionalInterface
public interface Comparator {
    boolean contains(Product product, String atribute);
}
