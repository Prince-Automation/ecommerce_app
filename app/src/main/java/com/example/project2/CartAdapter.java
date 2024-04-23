package com.example.project2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems;


    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;

    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.productNameTextView.setText(item.getProductName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));

        // Format the price with commas for better readability
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedPrice = numberFormat.format(item.getPricePerItem());
        holder.priceTextView.setText(String.format(Locale.getDefault(), "$%s", formattedPrice));

        holder.increaseQuantityButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            updateQuantity(item, item.getQuantity());
        });

        holder.decreaseQuantityButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateQuantity(item, item.getQuantity());
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            CartItem cartItem = cartItems.get(position);
            ((CartActivity) v.getContext()).removeItem(cartItem);
        });



        // Download image from Firebase Storage to local storage or cache
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(item.getImageUrl());
        File localFile;
        try {
            localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Image downloaded successfully, load it into the ImageView
                Picasso.get().load(localFile).into(holder.productImageView);
            }).addOnFailureListener(exception -> {
                // Handle any errors
                Log.e("ProductAdapter", "Failed to download image", exception);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateQuantity(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        notifyDataSetChanged();
    }


    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView quantityTextView;
        TextView priceTextView;
        ImageView productImageView;
        ImageButton increaseQuantityButton;
        ImageButton decreaseQuantityButton;
        ImageButton removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.cartproductNameTextView);
            quantityTextView = itemView.findViewById(R.id.cartproductQuantityTextView);
            priceTextView = itemView.findViewById(R.id.cartproductPriceTextView);
            productImageView = itemView.findViewById(R.id.cartproductImageView);
            increaseQuantityButton = itemView.findViewById(R.id.cartincreaseBtn);
            decreaseQuantityButton = itemView.findViewById(R.id.cartdecreaseBtn);
            removeButton = itemView.findViewById(R.id.cartremoveBtn);
        }
    }
}



