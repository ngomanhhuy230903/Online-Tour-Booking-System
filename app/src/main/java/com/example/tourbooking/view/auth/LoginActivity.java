package com.example.tourbooking.view.auth;

import android.content.Intent;
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

            if (username.equals(correctUsername) && password.equals(correctPassword)) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Chuyển sang MainActivity hoặc màn hình khác
                Intent intent = new Intent(this, DashBoardActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
        forgotPassword.setOnClickListener(v -> {
            // Ví dụ: mở ForgotPasswordActivity
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