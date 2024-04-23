package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class ProductDetailActivity extends AppCompatActivity {

    private Button addToCartButton;
    private Button increaseQuantityButton;
    private Button decreaseQuantityButton;
    private TextView quantityTextView;
    private Button goToCartButton;
    private ImageButton backButton;
    private ImageButton cartButton;
    private String productName;
    private String productDescription;
    private String productPrice;
    private String productImageUrl;
    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productDescriptionTextView;
    private TextView productPriceTextView;
    private CartAdapter CartAdapter;
    private static ProductDetailActivity instance;
    private int quantity = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Set the instance variable
        instance = this;

        // Initialize views
        backButton = findViewById(R.id.backButton);
        cartButton = findViewById(R.id.cartButton);

        // Initialize views
        addToCartButton = findViewById(R.id.addToCartButton);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);
        quantityTextView = findViewById(R.id.quantityTextView);
        goToCartButton = findViewById(R.id.goToCartButton);

        // Set initial visibility
        addToCartButton.setVisibility(View.VISIBLE);
        increaseQuantityButton.setVisibility(View.GONE);
        decreaseQuantityButton.setVisibility(View.GONE);
        quantityTextView.setVisibility(View.GONE);
        goToCartButton.setVisibility(View.GONE);

        CartAdapter = new CartAdapter(CartManager.getInstance().getCartItems());


        // Add to cart button click listener
        addToCartButton.setOnClickListener(v -> {
            addToCartButton.setVisibility(View.GONE);
            increaseQuantityButton.setVisibility(View.VISIBLE);
            decreaseQuantityButton.setVisibility(View.VISIBLE);
            quantityTextView.setVisibility(View.VISIBLE);
            goToCartButton.setVisibility(View.VISIBLE);

            // Remove "$" and "," from the product price string
            String cleanPrice = productPrice.replaceAll("[^\\d.]", "");

            // Parse the cleaned price string to a double
            double price = Double.parseDouble(cleanPrice);

            CartItem cartItem = new CartItem(productName, price, quantity, productImageUrl);
            CartManager.getInstance().addItem(cartItem);
            // Notify the user
            Toast.makeText(this, "Item added to cart", Toast.LENGTH_SHORT).show();
        });

        // Increase quantity button click listener
        increaseQuantityButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
            CartManager.getInstance().updateQuantity(productName, quantity);
            CartAdapter.notifyDataSetChanged();
        });

        // Decrease quantity button click listener
        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
                CartManager.getInstance().updateQuantity(productName, quantity);
                CartAdapter.notifyDataSetChanged();
            }
        });

        // Go to cart button click listener
        goToCartButton.setOnClickListener(v -> {
            // Navigate to the CartActivity
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Back button click listener
        backButton.setOnClickListener(v -> {
            // Navigate back to the ProductActivity
            finish();
        });

        // Cart button click listener
        cartButton.setOnClickListener(v -> {
            // Navigate to the CartActivity
            Intent intent = new Intent(ProductDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Get the product details from the intent
        productName = getIntent().getStringExtra("productName");
        productDescription = getIntent().getStringExtra("productDescription");
        productPrice = getIntent().getStringExtra("productPrice");
        productImageUrl = getIntent().getStringExtra("productImageUrl");

        // Initialize views
        productImageView = findViewById(R.id.productImageView);
        productNameTextView = findViewById(R.id.productNameTextView);
        productDescriptionTextView = findViewById(R.id.productDescriptionTextView);
        productPriceTextView = findViewById(R.id.productPriceTextView);
//        Button addToCartButton = findViewById(R.id.addToCartButton);

        // Set the product details to the views
        productNameTextView.setText(productName);
        productDescriptionTextView.setText(productDescription);
        productPriceTextView.setText(productPrice);

        // Check if the product is already in the cart
        CartItem cartItem = CartManager.getInstance().getCartItem(productName);
        if (cartItem != null) {
            quantity = cartItem.getQuantity();
            updateQuantityViews();
        }

//        // Fetch the image URL from the database
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("products");
//        databaseReference.orderByChild("name").equalTo(productName).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
//                        Picasso.get().load(imageUrl).into(productImageView);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("ProductDetailActivity", "Failed to read value.", error.toException());
//            }
//        });

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(productImageUrl);
        File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Image downloaded successfully, load it into the ImageView
                Picasso.get().load(localFile).into(productImageView);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e("ProductAdapter", "Failed to download image", exception);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updateQuantityViews() {
        addToCartButton.setVisibility(View.GONE);
        increaseQuantityButton.setVisibility(View.VISIBLE);
        decreaseQuantityButton.setVisibility(View.VISIBLE);
        quantityTextView.setVisibility(View.VISIBLE);
        goToCartButton.setVisibility(View.VISIBLE);
        quantityTextView.setText(String.valueOf(quantity));
    }

    public static ProductDetailActivity getInstance() {
        return instance;
    }

    public String getProductName() {
        return productName;
    }

    public void resetQuantity() {
        quantity = 1;
        quantityTextView.setText(String.valueOf(quantity));
        addToCartButton.setVisibility(View.VISIBLE);
        increaseQuantityButton.setVisibility(View.GONE);
        decreaseQuantityButton.setVisibility(View.GONE);
        quantityTextView.setVisibility(View.GONE);
        goToCartButton.setVisibility(View.GONE);
    }

}
