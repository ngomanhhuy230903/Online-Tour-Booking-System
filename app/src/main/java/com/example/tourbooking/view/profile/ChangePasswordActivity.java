package com.example.tourbooking.view.profile;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.example.tourbooking.R;
import com.example.tourbooking.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private TextInputLayout layoutConfirmPassword;
    private ProgressBar passwordStrengthIndicator, progressBar;
    private Button btnSaveChanges;

    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_change_password);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        passwordStrengthIndicator = findViewById(R.id.passwordStrengthIndicator);
        progressBar = findViewById(R.id.progressBarChangePassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void setupListeners() {
        btnSaveChanges.setOnClickListener(v -> validateAndSaveChanges());
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updatePasswordStrength(CharSequence s) {
        int passwordLength = s.length();
        if (passwordLength == 0) {
            passwordStrengthIndicator.setProgress(0);
        } else if (passwordLength < 6) {
            passwordStrengthIndicator.setProgress(25);
            passwordStrengthIndicator.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorError), PorterDuff.Mode.SRC_IN);
        } else if (passwordLength < 10) {
            passwordStrengthIndicator.setProgress(60);
            passwordStrengthIndicator.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorWarning), PorterDuff.Mode.SRC_IN);
        } else {
            passwordStrengthIndicator.setProgress(100);
            passwordStrengthIndicator.getProgressDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorSuccess), PorterDuff.Mode.SRC_IN);
        }
    }

    private void validateAndSaveChanges() {
        String currentPassword = etCurrentPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmNewPassword.getText().toString();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            layoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        } else {
            layoutConfirmPassword.setError(null);
        }

        setLoadingState(true);
        verifyCurrentPasswordAndProceed(currentPassword, newPassword);
    }

    private void verifyCurrentPasswordAndProceed(String currentPassword, String newPassword) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Phiên đăng nhập không hợp lệ.", Toast.LENGTH_SHORT).show();
            setLoadingState(false);
            return;
        }

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String storedPassword = documentSnapshot.getString("password");
                        if (storedPassword != null && storedPassword.equals(currentPassword)) {
                            updatePasswordInFirestore(userId, newPassword);
                        } else {
                            setLoadingState(false);
                            Toast.makeText(this, "Mật khẩu hiện tại không chính xác.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        setLoadingState(false);
                        Toast.makeText(this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updatePasswordInFirestore(String userId, String newPassword) {
        db.collection("users").document(userId)
                .update("password", newPassword)
                .addOnSuccessListener(aVoid -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Lỗi: không thể cập nhật mật khẩu.", Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSaveChanges.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSaveChanges.setEnabled(true);
        }
    }
}