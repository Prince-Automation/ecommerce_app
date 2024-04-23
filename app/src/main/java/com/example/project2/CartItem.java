package com.example.project2;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productName;
    private double pricePerItem;
    private int quantity;
    private String imageUrl;

    public CartItem(String productName, double pricePerItem, int quantity, String imageUrl) {
        this.productName = productName;
        this.pricePerItem = pricePerItem;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public double getPricePerItem() {
        return pricePerItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return pricePerItem * quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
