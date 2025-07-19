package com.example.tourbooking.view.profile;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.R;

import android.content.SharedPreferences;
import android.widget.EditText;

public class EditProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        EditText etFullName = findViewById(R.id.etFullName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etDob = findViewById(R.id.etDob);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString();
            String email = etEmail.getText().toString();
            String phone = etPhone.getText().toString();
            String dob = etDob.getText().toString();

            // Lưu vào SharedPreferences (hoặc thay bằng lưu lên server/database)
            SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
            prefs.edit()
                    .putString("fullName", fullName)
                    .putString("email", email)
                    .putString("phone", phone)
                    .putString("dob", dob)
                    .apply();

            finish();
        });
    }
}