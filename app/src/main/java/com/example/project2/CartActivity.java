package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private CartManager cartManager;
    private ImageView backButton;
    private TextView subtotalTextView;
    private TextView taxesTextView;
    private TextView estimatedTotalTextView;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cartrecyclerView);
        cartManager = CartManager.getInstance();

        List<CartItem> cartItems = cartManager.getCartItems();
        cartAdapter = new CartAdapter(cartItems);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cartAdapter);

        backButton = findViewById(R.id.cartbackBtn);
        subtotalTextView = findViewById(R.id.subtotalTextView);
        taxesTextView = findViewById(R.id.taxesTextView);
        estimatedTotalTextView = findViewById(R.id.estimatedTotalTextView);

        // Set click listener for the back button
        backButton.setOnClickListener(v -> {
            // Navigate back to the ProductsActivity
            Intent intent = new Intent(CartActivity.this, ProductActivity.class);
            startActivity(intent);
        });

        // Calculate and update the subtotal, taxes, and estimated total
        calculateTotals();

        checkoutButton = findViewById(R.id.checkoutBtn);
        checkoutButton.setOnClickListener(v -> {
            // Convert the list to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            byte[] cartItemsBytes = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(cartItems);
                out.flush();
                cartItemsBytes = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            // Start the CheckoutActivity
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("cartItems", cartItemsBytes);
            startActivity(intent);
        });
    }

    public void calculateTotals() {
        double subtotal = 0.0;
        for (CartItem item : cartManager.getCartItems()) {
            subtotal += item.getTotalPrice();
        }
        subtotalTextView.setText(formatCurrency(subtotal));

        double taxes = subtotal * 0.13;
        taxesTextView.setText(formatCurrency(taxes));

        double estimatedTotal = subtotal + taxes;
        estimatedTotalTextView.setText(formatCurrency(estimatedTotal));
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        return formatter.format(amount);
    }


    public void removeItem(CartItem item) {
        int position = -1;
        for (int i = 0; i < cartManager.getCartItems().size(); i++) {
            if (cartManager.getCartItems().get(i).getProductName().equals(item.getProductName())) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            cartManager.removeCartItem(item.getProductName());
            cartAdapter.notifyItemRemoved(position);
            cartAdapter.notifyDataSetChanged();

            // Check if the removed item is currently displayed in the ProductDetailActivity
            ProductDetailActivity productDetailActivity = ProductDetailActivity.getInstance();
            if (productDetailActivity != null && productDetailActivity.getProductName().equals(item.getProductName())) {
                // Reset the quantity and update the UI in the ProductDetailActivity
                productDetailActivity.resetQuantity();
            }
        }
    }

    public void checkout() {

    }

    public void updateQuantity(CartItem item, int newQuantity) {
        cartManager.updateQuantity(item.getProductName(), newQuantity);
        cartAdapter.notifyDataSetChanged();
    }


}

