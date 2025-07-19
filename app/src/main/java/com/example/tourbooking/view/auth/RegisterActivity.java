package com.example.tourbooking.view.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tourbooking.R;
import com.example.tourbooking.view.info.TermsActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullNameInput, emailInput, phoneInput, passwordInput, confirmPasswordInput;
    private DatePicker dateOfBirthPicker;
    private RadioGroup genderRadioGroup;
    private CheckBox termsCheckbox;
    private Button registerButton;
    private TextView loginRedirectLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Ánh xạ
        fullNameInput = findViewById(R.id.fullNameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        dateOfBirthPicker = findViewById(R.id.dateOfBirthPicker);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        termsCheckbox = findViewById(R.id.termsCheckbox);
        registerButton = findViewById(R.id.registerButton);
        loginRedirectLink = findViewById(R.id.loginRedirectLink);

        // Nhấn vào checkbox để xem điều khoản
        termsCheckbox.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsActivity.class);
            startActivity(intent);
        });

        // Đăng ký
        registerButton.setOnClickListener(v -> {
            String fullName = fullNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim(); // dùng làm userId
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Giới tính
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            String gender = (selectedGenderId == R.id.genderMale) ? "Male" : "Female";

            // Ngày sinh
            int day = dateOfBirthPicker.getDayOfMonth();
            int month = dateOfBirthPicker.getMonth() + 1;
            int year = dateOfBirthPicker.getYear();
            String dob = day + "/" + month + "/" + year;

            // Ràng buộc
            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!termsCheckbox.isChecked()) {
                Toast.makeText(this, "You must agree to the terms", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi lên Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> user = new HashMap<>();
            user.put("userId", email); // dùng email làm ID
            user.put("userName", email);
            user.put("fullName", fullName);
            user.put("phoneNumber", phone);
            user.put("password", password);
            user.put("dob", dob);
            user.put("gender", gender);

            db.collection("users")
                    .document(email) // dùng email làm document ID
                    .set(user)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Chuyển sang login
        loginRedirectLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
