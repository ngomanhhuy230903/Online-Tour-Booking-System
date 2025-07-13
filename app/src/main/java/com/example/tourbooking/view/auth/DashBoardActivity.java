package com.example.tourbooking.view.auth;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourbooking.R;
import com.example.tourbooking.SettingsActivity;
import com.example.tourbooking.NotificationsActivity;
import com.example.tourbooking.ChatSupportActivity;
import com.example.tourbooking.FeedbackActivity;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        ImageButton btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, com.example.tourbooking.view.profile.ProfileActivity.class));
        });

        ImageButton btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        ImageButton btnNotifications = findViewById(R.id.btnNotifications);
        btnNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        ImageButton btnChat = findViewById(R.id.btnChat);
        btnChat.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatSupportActivity.class));
        });

        ImageButton btnFeedback = findViewById(R.id.btnFeedback);
        btnFeedback.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
        });
    }
}