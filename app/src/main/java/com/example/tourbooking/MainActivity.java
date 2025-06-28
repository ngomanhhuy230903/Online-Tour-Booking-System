package com.example.tourbooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Import các Activity cần thiết
import com.example.tourbooking.view.booking.BookingHistoryActivity;
import com.example.tourbooking.view.booking.FavoritesActivity;
import com.example.tourbooking.view.booking.PaymentActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Nút Test màn hình Thanh toán (M15)
        Button testPaymentButton = findViewById(R.id.btnTestPayment);
        testPaymentButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            startActivity(intent);
        });

        // === BẮT ĐẦU PHẦN CODE MỚI ===

        // Nút Test màn hình Lịch sử đặt tour (M17)
        Button testHistoryButton = findViewById(R.id.btnTestHistory);
        testHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        });

        // Nút Test màn hình Yêu thích (M19)
        Button testFavoritesButton = findViewById(R.id.btnTestFavorites);
        testFavoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        // === KẾT THÚC PHẦN CODE MỚI ===


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}