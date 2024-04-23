package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    private Button continueShoppingButton;
    private TextView totalAmountTextView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Receive the byte array
        byte[] cartItemsBytes = getIntent().getByteArrayExtra("cartItems");

        // Deserialize the byte array back to a list
        ArrayList<CartItem> cartItemList = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(cartItemsBytes);
            ObjectInput in = new ObjectInputStream(bis);
            cartItemList = (ArrayList<CartItem>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Use the deserialized list in your activity
        if (cartItemList != null) {
            // Initialize RecyclerView and set the adapter
            recyclerView = findViewById(R.id.recyclerView);
            ReceiptAdapter receiptAdapter = new ReceiptAdapter(cartItemList);
            recyclerView.setAdapter(receiptAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Calculate and display the total amount including taxes
            double subtotal = 0.0;
            for (CartItem item : cartItemList) {
                subtotal += item.getTotalPrice();
            }

            double taxes = subtotal * 0.13; // tax rate is 13%
            double totalAmount = subtotal + taxes;

            totalAmountTextView = findViewById(R.id.receiptestimatedTotalTextView);
            totalAmountTextView.setText(formatCurrency(totalAmount));
        }


        continueShoppingButton = findViewById(R.id.continueShoppingButton);
        continueShoppingButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptActivity.this, ProductActivity.class);
            startActivity(intent);
            finish(); // Closing the ReceiptActivity to prevent going back to it with the back button
            // Clear the cart when going back to ProductsActivity
            CartManager.getInstance().clearCart();
        });

    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String formattedAmount = formatter.format(amount);
        Log.d("ReceiptActivity", "Formatted amount: " + formattedAmount);
        return formattedAmount;
    }
}
