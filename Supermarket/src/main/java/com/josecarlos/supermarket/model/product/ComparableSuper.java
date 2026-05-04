package com.josecarlos.supermarket.model.product;

import com.josecarlos.supermarket.model.hash.Almacenable;

/**
 *
 * @author LENOVO
 * @param <T>
 */
public interface ComparableSuper<T> extends Almacenable<T> {
    /**
     * Compara el nombre y el codigo de barras
     * 
     * @param other
     * @return 
     */
    int compareName(T other);
    /**
     * Compara la fecha de expiracion y el codigo de barras
     * 
     * @param other
     * @return 
     */
    int compareDate(T other);
    /**
     * Compara la categoria y el codigo de barras
     * 
     * @param other
     * @return 
     */
    int compareCategory(T other);
}
