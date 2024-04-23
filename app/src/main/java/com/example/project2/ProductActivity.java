package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private DatabaseReference databaseReference;
    private ImageButton backButton;
    private ImageButton cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.productsrecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize product list
        productList = new ArrayList<>();

        // Initialize adapter
        adapter = new ProductAdapter(this, productList);

        // Set adapter to RecyclerView
        recyclerView.setAdapter(adapter);

        // Check if the user is authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is authenticated, fetch data from Firebase
            fetchDataFromFirebase();
        } else {

        }

        // Get a reference to the cart button
        cartButton = findViewById(R.id.productcartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the CartActivity
                Intent intent = new Intent(ProductActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Get a reference to the back button
        backButton = findViewById(R.id.productbackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log out the user
                FirebaseAuth.getInstance().signOut();

                // Navigate to the login activity
                Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
                startActivity(intent);
                finish(); // Finish the current activity
            }
        });


    }

    private void fetchDataFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String description = dataSnapshot.child("description").getValue(String.class);
                    String price = dataSnapshot.child("price").getValue(String.class);
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                    Log.d("ProductActivity", "Image URL: " + imageUrl);

                    Product product = new Product(name, description, price, imageUrl);
                    productList.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductActivity", "Failed to read value.", error.toException());
            }
        });
    }

}

