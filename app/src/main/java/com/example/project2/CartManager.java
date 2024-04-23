package com.example.project2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private HashMap<String, CartItem> cartItems;

    private CartManager() {
        cartItems = new HashMap<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(CartItem item) {
        if (cartItems.containsKey(item.getProductName())) {
            // If item already exists in cart, update quantity
            CartItem existingItem = cartItems.get(item.getProductName());
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            cartItems.put(item.getProductName(), item);
        }
    }

    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems.values());
    }

    public void clearCart() {
        cartItems.clear();
    }

    public void removeCartItem(String productName) {
        cartItems.remove(productName);
    }

    public void updateQuantity(String productName, int quantity) {
        if (cartItems.containsKey(productName)) {
            cartItems.get(productName).setQuantity(quantity);
        }
    }

    public CartItem getCartItem(String productName) {
        return cartItems.get(productName);
    }


}

