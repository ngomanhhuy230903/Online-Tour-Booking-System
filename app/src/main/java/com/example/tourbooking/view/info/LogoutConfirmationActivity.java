package com.example.tourbooking.view.info;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.R;
import com.example.tourbooking.view.auth.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogoutConfirmationActivity extends AppCompatActivity {
    private Switch rememberMeToggle;
    private SharedPreferences prefs;
    private static final String PREF_REMEMBER_ME = "remember_me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_confirmation);

        ImageView warningIcon = findViewById(R.id.warningIcon);
        TextView confirmationMessage = findViewById(R.id.confirmationMessage);
        TextView sessionInfo = findViewById(R.id.sessionInfo);
        TextView timestamp = findViewById(R.id.timestamp);
        rememberMeToggle = findViewById(R.id.rememberMeToggle);
        Button supportContactLink = findViewById(R.id.supportContactLink);
        Button noButton = findViewById(R.id.noButton);
        Button yesButton = findViewById(R.id.yesButton);
        LinearLayout bottomButtons = findViewById(R.id.bottomButtons);

        // Lấy thông tin user/session giả lập
        String userName = "John Doe";
        String sessionTime = new SimpleDateFormat("hh:mm a z, MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        String lastActive = new SimpleDateFormat("hh:mm a z", Locale.getDefault())
                .format(new Date(System.currentTimeMillis() - 3 * 60 * 1000));
        sessionInfo.setText("User: " + userName + " | Session: " + sessionTime);
        timestamp.setText("Last active: " + lastActive);

        // Remember Me toggle
        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean rememberMe = prefs.getBoolean(PREF_REMEMBER_ME, false);
        rememberMeToggle.setChecked(rememberMe);
        rememberMeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_REMEMBER_ME, isChecked).apply();
            Toast.makeText(this, isChecked ? "Will remember you next time" : "Will not remember you",
                    Toast.LENGTH_SHORT).show();
        });

        // Contact Support
        supportContactLink.setOnClickListener(v -> {
            // Điều hướng đến màn hình hỗ trợ (giả lập: Toast)
            Toast.makeText(this, "Contacting support...", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, SupportActivity.class));
        });

        // No: Đóng màn hình xác nhận
        noButton.setOnClickListener(v -> finish());

        // Yes: Đăng xuất và về LoginActivity
        yesButton.setOnClickListener(v -> {
            // Xóa Remember Me và userId khi đăng xuất
            prefs.edit()
                    .remove(PREF_REMEMBER_ME)
                    .remove("userId")
                    .apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}