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
import com.example.tourbooking.view.review.ReviewFormActivity;
import com.example.tourbooking.view.review.ReviewsListActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // ... (Code cho các nút cũ giữ nguyên)
        Button testPaymentButton = findViewById(R.id.btnTestPayment);
        testPaymentButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            startActivity(intent);
        });

        Button testHistoryButton = findViewById(R.id.btnTestHistory);
        testHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        });

        Button testFavoritesButton = findViewById(R.id.btnTestFavorites);
        testFavoritesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });

        Button testReviewButton = findViewById(R.id.btnTestReviewForm);
        testReviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReviewFormActivity.class);
            intent.putExtra("TOUR_ID_EXTRA", "ioqvj18Na7GKrERhKZbv"); // ID tour của bạn
            startActivity(intent);
        });

        // Nút Test màn hình Danh sách Đánh giá (M21)
        Button testReviewsListButton = findViewById(R.id.btnTestReviewsList);
        testReviewsListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ReviewsListActivity.class);
            // ĐÃ THAY THẾ ID TOUR CỦA BẠN VÀO ĐÂY
            intent.putExtra("TOUR_ID_EXTRA", "ioqvj18Na7GKrERhKZbv");
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}