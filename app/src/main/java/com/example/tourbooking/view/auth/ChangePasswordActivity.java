package com.example.tourbooking.view.auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.R;

public class ChangePasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        EditText etCurrent = findViewById(R.id.etCurrentPassword);
        EditText etNew = findViewById(R.id.etNewPassword);
        EditText etConfirm = findViewById(R.id.etConfirmPassword);
        TextView tvStrength = findViewById(R.id.tvPasswordStrength);
        TextView tvError = findViewById(R.id.tvError);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);
        TextView tvShowCurrent = findViewById(R.id.tvShowCurrent);

        // Ẩn thông báo lỗi khi vào màn hình
        tvError.setVisibility(View.GONE);

        // Show/Hide password cho current password
        tvShowCurrent.setOnClickListener(v -> {
            if (etCurrent.getTransformationMethod() instanceof PasswordTransformationMethod) {
                etCurrent.setTransformationMethod(null);
                tvShowCurrent.setText("Hide");
            } else {
                etCurrent.setTransformationMethod(PasswordTransformationMethod.getInstance());
                tvShowCurrent.setText("Show");
            }
            etCurrent.setSelection(etCurrent.getText().length());
        });

        btnCancel.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String current = etCurrent.getText().toString();
            String newPass = etNew.getText().toString();
            String confirm = etConfirm.getText().toString();

            SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
            String savedPassword = prefs.getString("password", "123456");

            // Kiểm tra mật khẩu hiện tại
            if (!current.equals(savedPassword)) {
                tvError.setText("Current password is incorrect");
                tvError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            // Kiểm tra xác nhận mật khẩu mới
            if (!newPass.equals(confirm)) {
                tvError.setText("Passwords do not match");
                tvError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            // Kiểm tra độ dài mật khẩu mới
            if (newPass.length() < 6) {
                tvError.setText("Password too short");
                tvError.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            // Đánh giá độ mạnh mật khẩu (ví dụ đơn giản)
            if (newPass.length() >= 8 && newPass.matches(".*[A-Z].*") && newPass.matches(".*[0-9].*")) {
                tvStrength.setText("Password strength: Strong");
                tvStrength.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvStrength.setText("Password strength: Weak");
                tvStrength.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }

            // Lưu mật khẩu mới
            prefs.edit().putString("password", newPass).apply();
            tvError.setVisibility(View.GONE);
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}