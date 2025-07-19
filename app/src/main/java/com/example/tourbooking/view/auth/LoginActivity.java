package com.example.tourbooking.view.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tourbooking.MainActivity;
import com.example.tourbooking.R;

import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;

    // Dữ liệu mẫu để kiểm tra
    private final String correctUsername = "admin";
    private final String correctPassword = "123456";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        TextView forgotPassword = findViewById(R.id.textViewForgotPassword);
        TextView register = findViewById(R.id.textViewRegister);

        buttonLogin = findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(view -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
            String savedUsername = prefs.getString("fullName", "admin");
            String savedPassword = prefs.getString("password", "123456");

            db.collection("users")
                    .whereEqualTo("userName", username)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            com.google.firebase.firestore.DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            String storedPassword = document.getString("password");

                            if (storedPassword != null && storedPassword.equals(password)) {
                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

                                String userId = document.getId();
                                String user = document.getString("userName");
                                String fullName = document.getString("fullName");

                                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString("userId", userId);
                                editor.putString("username", user);
                                editor.putString("fullName", fullName);
                                editor.apply();
                                Intent intent = new Intent(this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Login error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        forgotPassword.setOnClickListener(v -> {
            // Ví dụ: mở ForgotPasswordActivity
            SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
            prefs.edit().putString("password", "123456").apply();
            Toast.makeText(this, "Password has been reset to 123456", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        register.setOnClickListener(v -> {
            // Ví dụ: mở RegisterActivity
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}