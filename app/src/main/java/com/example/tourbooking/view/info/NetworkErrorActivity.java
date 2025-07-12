package com.example.tourbooking.view.info;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NetworkErrorActivity extends AppCompatActivity {
    private Switch offlineModeToggle;
    private SharedPreferences prefs;
    private static final String PREF_OFFLINE_MODE = "offline_mode";
    private TextView lastSyncTime, appVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m35);

        ImageView errorIcon = findViewById(R.id.errorIcon);
        TextView errorMessage = findViewById(R.id.errorMessage);
        Button retryButton = findViewById(R.id.retryButton);
        offlineModeToggle = findViewById(R.id.offlineModeToggle);
        lastSyncTime = findViewById(R.id.lastSyncTime);
        appVersion = findViewById(R.id.appVersion);
        Button contactSupportLink = findViewById(R.id.contactSupportLink);
        ImageView backgroundImage = findViewById(R.id.backgroundImage);

        // Hiển thị thời gian đồng bộ cuối cùng
        String syncTime = new SimpleDateFormat("hh:mm a z, MMMM dd, yyyy", Locale.getDefault()).format(new Date());
        lastSyncTime.setText("Last sync: " + syncTime);

        // Hiển thị phiên bản ứng dụng
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            appVersion.setText("App Version: " + version);
        } catch (Exception e) {
            appVersion.setText("App Version: 1.0.0");
        }

        // Offline Mode toggle
        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean offlineMode = prefs.getBoolean(PREF_OFFLINE_MODE, false);
        offlineModeToggle.setChecked(offlineMode);
        offlineModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(PREF_OFFLINE_MODE, isChecked).apply();
            Toast.makeText(this, isChecked ? "Offline mode enabled" : "Offline mode disabled", Toast.LENGTH_SHORT)
                    .show();
            // TODO: Hiển thị dữ liệu offline nếu có
        });

        // Retry button
        retryButton.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                Toast.makeText(this, "Connection restored!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng màn lỗi, quay lại màn trước
            } else {
                Toast.makeText(this, "Still no connection.", Toast.LENGTH_SHORT).show();
            }
        });

        // Contact Support
        contactSupportLink.setOnClickListener(v -> {
            Toast.makeText(this, "Contacting support...", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, SupportActivity.class));
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}