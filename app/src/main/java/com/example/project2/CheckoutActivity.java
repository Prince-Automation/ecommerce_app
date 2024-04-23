package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private TextView contactDetailsTextView;
    private TextView shippingAddressTextView;
    private TextView cardDetailsTextView;
    private LinearLayout contactDetailsLayout;
    private LinearLayout shippingAddressLayout;
    private LinearLayout cardDetailsLayout;
    private ImageView checkoutbackBtn;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        contactDetailsTextView = findViewById(R.id.contactdetailsTextView);
        shippingAddressTextView = findViewById(R.id.shippingaddressTextView);
        cardDetailsTextView = findViewById(R.id.carddetailsTextView);
        contactDetailsLayout = findViewById(R.id.contactDetailsLayout);
        shippingAddressLayout = findViewById(R.id.shippingDetailsLayout);
        cardDetailsLayout = findViewById(R.id.cardDetailsLayout);
        checkoutbackBtn = findViewById(R.id.checkoutbackBtn);
        submitButton = findViewById(R.id.checkoutsubmitbutton);

        // Set click listeners
        contactDetailsTextView.setOnClickListener(v -> toggleVisibility(contactDetailsLayout));
        shippingAddressTextView.setOnClickListener(v -> toggleVisibility(shippingAddressLayout));
        cardDetailsTextView.setOnClickListener(v -> toggleVisibility(cardDetailsLayout));

        checkoutbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitButton.setOnClickListener(v -> {
            if (validateFields()) {
                // All fields are valid, proceed with checkout
                Toast.makeText(CheckoutActivity.this, "Checkout successful!", Toast.LENGTH_SHORT).show();
                ArrayList<CartItem> cartItems = new ArrayList<>(CartManager.getInstance().getCartItems());

                // Serialize your list of CartItem objects to a byte array
                byte[] cartItemsBytes = serializeCartItems(cartItems);

                // Start the ReceiptActivity and pass the byte array
                Intent intent = new Intent(CheckoutActivity.this, ReceiptActivity.class);
                intent.putExtra("cartItems", cartItemsBytes);
                startActivity(intent);
                finish(); // Finish the current activity
            } else {
                Toast.makeText(CheckoutActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean validateFields() {
        boolean isValid = true;

        // Validate contact details
        EditText contactNameEditText = findViewById(R.id.fullnameText);
        EditText contactEmailEditText = findViewById(R.id.editTextEmailAddress);
        EditText contactPhoneEditText = findViewById(R.id.editTextPhonenumber);
        if (!isValidPhoneNumber(contactPhoneEditText.getText().toString())) {
            isValid = false;
            contactPhoneEditText.setError("Invalid phone number");
        }
        if (!isValidEmail(contactEmailEditText.getText().toString())) {
            isValid = false;
            contactEmailEditText.setError("Invalid email address");
        }

        // Validate shipping address
        EditText countryEditText = findViewById(R.id.countryEditText);
        EditText stateEditText = findViewById(R.id.stateEditText);
        EditText shippingCityEditText = findViewById(R.id.cityEditText);
        EditText shippingPostalCodeEditText = findViewById(R.id.postalcodeEditText);
        if (!isValidAlphabetical(countryEditText.getText().toString())) {
            isValid = false;
            countryEditText.setError("Invalid country name");
        }
        if (!isValidAlphabetical(stateEditText.getText().toString())) {
            isValid = false;
            stateEditText.setError("Invalid state name");
        }
        if (!isValidAlphabetical(shippingCityEditText.getText().toString())) {
            isValid = false;
            shippingCityEditText.setError("Invalid city name");
        }
        if (!isValidPostalCode(shippingPostalCodeEditText.getText().toString())) {
            isValid = false;
            shippingPostalCodeEditText.setError("Invalid postal code");
        }

        // Validate card details
        EditText cardNumberEditText = findViewById(R.id.cardnumberEditText);
        EditText cardmonthExpiryEditText = findViewById(R.id.expirymontheditText);
        EditText cardyearExpiryEditText = findViewById(R.id.expiryyeareditText);
        EditText cardCvvEditText = findViewById(R.id.cvveditText);
        if (!isValidCardNumber(cardNumberEditText.getText().toString())) {
            isValid = false;
            cardNumberEditText.setError("Invalid card number");
        }
        if (!isValidExpiryMonth(cardmonthExpiryEditText.getText().toString())) {
            isValid = false;
            cardmonthExpiryEditText.setError("Invalid expiry month");
        }
        if (!isValidExpiryYear(cardyearExpiryEditText.getText().toString())) {
            isValid = false;
            cardyearExpiryEditText.setError("Invalid expiry year");
        }
        if (!isValidCVV(cardCvvEditText.getText().toString())) {
            isValid = false;
            cardCvvEditText.setError("Invalid CVV");
        }

        return isValid;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^\\d{10}$");
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidAlphabetical(String text) {
        return text.matches("^[a-zA-Z]+$");
    }

    private boolean isValidPostalCode(String postalCode) {
        return postalCode.matches("^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$");
    }

    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches("^\\d{16}$");
    }

    private boolean isValidExpiryMonth(String expiryMonth) {
        return expiryMonth.matches("^\\d{2}$");
    }

    private boolean isValidExpiryYear(String expiryYear) {
        return expiryYear.matches("^\\d{4}$");
    }

    private boolean isValidCVV(String cvv) {
        return cvv.matches("^\\d{3}$");
    }


    private void toggleVisibility(LinearLayout layout) {
        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
        }
    }

    private byte[] serializeCartItems(List<CartItem> cartItems) {
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
        return cartItemsBytes;
    }

}
