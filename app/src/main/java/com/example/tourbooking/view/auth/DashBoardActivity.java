package com.example.tourbooking.view.auth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.ImageButton;
import com.example.tourbooking.R;

public class DashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
      ImageButton btnProfile = findViewById(R.id.btnProfile);
    btnProfile.setOnClickListener(v -> {
        startActivity(new Intent(this, com.example.tourbooking.view.profile.ProfileActivity.class));
    });
    }
}