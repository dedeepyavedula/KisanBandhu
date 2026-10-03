package com.example.kisanbandhu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kisanbandhu.MainActivity;
import com.example.kisanbandhu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword;
    private Button btnRegister;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Connect XML elements
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);

        btnRegister = findViewById(R.id.btnRegister);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter your name");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Enter your phone number");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter a password");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must contain at least 6 characters");
            return;
        }

        btnRegister.setEnabled(false);

        // Create Firebase Authentication account
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (!task.isSuccessful()) {

                        btnRegister.setEnabled(true);

                        Toast.makeText(
                                RegisterActivity.this,
                                "Registration failed: "
                                        + task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    // Get unique Firebase user ID
                    String userId = auth.getCurrentUser().getUid();

                    // User information for Firestore
                    Map<String, Object> user = new HashMap<>();

                    user.put("name", name);
                    user.put("email", email);
                    user.put("phone", phone);
                    user.put("role", "farmer");

                    // Save user information
                    db.collection("users")
                            .document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Account created successfully!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                // Go to Home
                                Intent intent = new Intent(
                                        RegisterActivity.this,
                                        MainActivity.class
                                );

                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {

                                btnRegister.setEnabled(true);

                                Toast.makeText(
                                        RegisterActivity.this,
                                        "Account created, but profile could not be saved.",
                                        Toast.LENGTH_LONG
                                ).show();
                            });
                });
    }
}