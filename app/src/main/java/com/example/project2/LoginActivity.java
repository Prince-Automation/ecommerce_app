package com.example.project2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageButton backButton;

//    private ImageView imageView;
//    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emaileditTxt);
        passwordEditText = findViewById(R.id.passeditTxt);
        Button loginButton = findViewById(R.id.loginBtn);
        TextView signupTextView = findViewById(R.id.signuptxtView);
        backButton = findViewById(R.id.backBtn);

//        imageView = findViewById(R.id.imageView);
//        storageReference = FirebaseStorage.getInstance().getReference().child("bombay_studio_logo.png");
//
//        // Download image from Firebase Storage
//        final long ONE_MEGABYTE = 1024 * 1024;
//        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Convert bytes to Bitmap
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                imageView.setImageBitmap(bitmap);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//            }
//        });

        // Set OnClickListener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish(); // Finish the LoginActivity so it's not kept in the back stack
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Validate email format
                if (!isValidEmail(email)) {
                    emailEditText.setError("Invalid email format");
                    return;
                }

                // Validate email and password
                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    return;
                }

                // Sign in user with Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginActivity.this, "Authentication successful.",
                                            Toast.LENGTH_SHORT).show();
                                    // Navigate to the products page
                                    startActivity(new Intent(LoginActivity.this, ProductActivity.class));
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed. Please check your credentials.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        // Navigate to signup activity when "Not a member? Join Now" is clicked
        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear the text fields
        emailEditText.setText("");
        passwordEditText.setText("");
    }

    private boolean isValidEmail(String email) {
        // Regex pattern for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Return true if email matches the pattern, false otherwise
        return email.matches(emailPattern);
    }
}
