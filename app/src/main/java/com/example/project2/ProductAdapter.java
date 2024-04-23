package com.example.project2;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String shortName = product.getName().length() > 20 ? product.getName().substring(0, 20) + "..." : product.getName();
        String shortDescription = product.getDescription().length() > 50 ? product.getDescription().substring(0, 50) + "..." : product.getDescription();

        holder.nameTextView.setText(shortName);
        holder.descriptionTextView.setText(shortDescription);
        holder.priceTextView.setText(product.getPrice());

        // Set the quantity text and visibility based on the product's quantity
//        if (product.getQuantity() > 0) {
//            holder.quantityTextView.setText(String.valueOf(product.getQuantity()));
//            holder.quantityTextView.setVisibility(View.VISIBLE);
//            holder.addButton.setVisibility(View.GONE);
//            holder.quantityLayout.setVisibility(View.VISIBLE);
//        } else {
//            holder.quantityTextView.setVisibility(View.GONE);
//            holder.addButton.setVisibility(View.VISIBLE);
//            holder.quantityLayout.setVisibility(View.GONE);
//        }

        // Download image from Firebase Storage to local storage or cache
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(product.getImageUrl());
        File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Image downloaded successfully, load it into the ImageView
                Picasso.get().load(localFile).into(holder.imageView);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e("ProductAdapter", "Failed to download image", exception);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

//        holder.addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int quantity = product.getQuantity();
//                quantity++;
//                product.setQuantity(quantity);
//                notifyDataSetChanged(); // Notify RecyclerView that data has changed
//            }
//        });

        // Increase quantity button
//        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Increment the quantity and update the UI
//                int quantity = product.getQuantity();
//                quantity++;
//                product.setQuantity(quantity);
//                notifyDataSetChanged(); // Notify RecyclerView that data has changed
//            }
//        });

        // Decrease quantity button
//        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Decrease the quantity if it's greater than 0 and update the UI
//                int quantity = product.getQuantity();
//                if (quantity > 0) {
//                    quantity--;
//                    product.setQuantity(quantity);
//                    notifyDataSetChanged(); // Notify RecyclerView that data has changed
//                }
//            }
//        });

        final int pos = position;
        // Set click listener for the card view
        holder.itemView.setOnClickListener(v -> {
            // Get the selected product
            Product products = productList.get(pos);

            // Create intent to navigate to ProductDetailsActivity
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("productName", products.getName());
            intent.putExtra("productDescription", products.getDescription());
            intent.putExtra("productPrice", products.getPrice());
            intent.putExtra("productImageUrl", products.getImageUrl());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameTextView, descriptionTextView, priceTextView, quantityTextView;
        Button addButton, increaseButton, decreaseButton;
        LinearLayout quantityLayout;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productimageView);
            nameTextView = itemView.findViewById(R.id.productnameTextView);
            descriptionTextView = itemView.findViewById(R.id.proddescriptionTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            addButton = itemView.findViewById(R.id.addButton);
//            increaseButton = itemView.findViewById(R.id.increaseButton);
//            decreaseButton = itemView.findViewById(R.id.decreaseButton);
//            quantityTextView = itemView.findViewById(R.id.quantityTextView);
//            quantityLayout = itemView.findViewById(R.id.quantityLayout);
        }
    }
}
