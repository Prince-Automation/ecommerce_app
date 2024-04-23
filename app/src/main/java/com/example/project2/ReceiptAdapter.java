package com.example.project2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {
    private List<CartItem> cartItems;

    public ReceiptAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receipt_layout, parent, false);
        return new ReceiptViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.productNameTextView.setText(item.getProductName());
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
//        if (holder.totalAmountTextView != null) {
//            holder.totalAmountTextView.setText(formatCurrency(item.getTotalPrice()));
//        }


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

    public static class ReceiptViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;
        TextView quantityTextView;
//        TextView totalAmountTextView;

        public ReceiptViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.receiptproductImageView);
            productNameTextView = itemView.findViewById(R.id.receiptproductNameTextView);
            quantityTextView = itemView.findViewById(R.id.receiptQuantityTextView);
//            totalAmountTextView = itemView.findViewById(R.id.receiptestimatedTotalTextView);
        }
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        String formattedAmount = formatter.format(amount);
        Log.d("ReceiptAdapter", "Formatted amount: " + formattedAmount);
        return formattedAmount;
    }

}


