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
import androidx.annotation.Nullable;

import com.example.tourbooking.MainActivity;
import com.example.tourbooking.R;
import com.example.tourbooking.view.info.TermsActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;

    // Dữ liệu mẫu để kiểm tra
    private final String correctUsername = "admin";
    private final String correctPassword = "123456";

    private static final int REQUEST_TERMS = 100;

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
                // Mở TermsActivity để người dùng chấp nhận điều khoản trước khi vào Dashboard
                Intent intent = new Intent(this, TermsActivity.class);
                startActivityForResult(intent, REQUEST_TERMS);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TERMS) {
            if (resultCode == RESULT_OK) {
                // Người dùng đã chấp nhận điều khoản, vào Dashboard
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "You must accept terms to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }
}