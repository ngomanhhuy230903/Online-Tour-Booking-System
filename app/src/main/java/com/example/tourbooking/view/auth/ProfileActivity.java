package com.example.tourbooking.view.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.R;

import de.hdodenhof.circleimageview.CircleImageView;

import android.widget.Toast;
import android.content.SharedPreferences;
import com.example.tourbooking.view.auth.ChangePasswordActivity;
public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvDob = findViewById(R.id.tvDob);

        SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "John Doe");
        String email = prefs.getString("email", "johndoe@email.com");
        String phone = prefs.getString("phone", "+1 234 567 8901");
        String dob = prefs.getString("dob", "January 15, 1990");

        tvFullName.setText(fullName);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvDob.setText(dob);

        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            // Xử lý logout tại đây
            finish();
        });
        Button btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhone = findViewById(R.id.tvPhone);
        TextView tvDob = findViewById(R.id.tvDob);

        SharedPreferences prefs = getSharedPreferences("profile", MODE_PRIVATE);
        tvFullName.setText(prefs.getString("fullName", "John Doe"));
        tvEmail.setText(prefs.getString("email", "johndoe@email.com"));
        tvPhone.setText(prefs.getString("phone", "+1 234 567 8901"));
        tvDob.setText(prefs.getString("dob", "January 15, 1990"));
    }
}