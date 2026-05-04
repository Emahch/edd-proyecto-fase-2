package com.josecarlos.supermarket.model.product;

/**
 *
 * @author LENOVO
 */
public class Product implements ComparableSuper<Product>{
    
    private String name;
    private String barcode;
    private String category;
    private String expireDate;
    private String brand;
    private double price;
    private int stock;

    public Product(String name, String barcode, String category, String expireDate, String brand, double price, int stock) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.expireDate = expireDate;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
    }

    public Product() {
    }
    
    @Override
    public int compareName(Product other) {
        int compareResult = name.toLowerCase().compareTo(other.name.toLowerCase());
        
        if (compareResult == 0) {
            return compareTo(other);
        }
        return compareResult;
    }

    @Override
    public int compareDate(Product other) {
        int compareResult = expireDate.toLowerCase().compareTo(other.expireDate.toLowerCase());
        
        if (compareResult == 0) {
            return compareTo(other);
        }
        return compareResult;
    }

    @Override
    public int compareCategory(Product other) {
        int compareResult = category.toLowerCase().compareTo(other.category.toLowerCase());
        
        if (compareResult == 0) {
            return compareTo(other);
        }
        return compareResult;
    }

    @Override
    public int compareTo(Product other) {
        return barcode.compareTo(other.barcode);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String getKey() {
        return barcode;
    }
}
