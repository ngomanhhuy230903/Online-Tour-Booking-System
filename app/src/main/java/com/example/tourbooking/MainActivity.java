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
import com.example.tourbooking.view.tour.CompareTourActivity;
import com.example.tourbooking.view.tour.DashBoardActivity;
import com.example.tourbooking.view.auth.LoginActivity;
import com.example.tourbooking.view.booking.BookingHistoryActivity;
import com.example.tourbooking.view.booking.FavoritesActivity;
import com.example.tourbooking.view.booking.PaymentActivity;
import com.example.tourbooking.view.home.HomeActivity;
import com.example.tourbooking.view.review.ReviewFormActivity;
import com.example.tourbooking.view.review.ReviewsListActivity;
import com.example.tourbooking.view.tour.ItineraryBuilderActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // HomePage
        Button btnHomepage = findViewById(R.id.btnTestHomePage);
        btnHomepage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Nút Test màn hình Thanh toán (M15)
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

        Button testLogin = findViewById(R.id.btnTestLogin);
        testLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        Button testDashb = findViewById(R.id.btnTestDashb);
        testDashb.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DashBoardActivity.class);
            startActivity(intent);
        });

        Button testIti = findViewById(R.id.btnTestIti);
        testIti.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ItineraryBuilderActivity.class);
            startActivity(intent);
        });
        Button testCompare = findViewById(R.id.btnTestCompare);
        testCompare.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompareTourActivity.class);
            startActivity(intent);
        });
        Button testInsur = findViewById(R.id.btnTestInsur);
        testInsur.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompareTourActivity.class);
            startActivity(intent);
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}