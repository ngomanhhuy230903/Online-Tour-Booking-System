package com.example.tourbooking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.tourbooking.view.booking.PaymentActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --- BẮT ĐẦU PHẦN CODE ĐƯỢC THÊM VÀO ---

        // 1. Tìm nút bấm bằng ID của nó
        Button testPaymentButton = findViewById(R.id.btnTestPayment);

        // 2. Thiết lập sự kiện khi nút được nhấn
        testPaymentButton.setOnClickListener(v -> {
            // Tạo một Intent để mở PaymentActivity
            Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
            // Bắt đầu Activity mới
            startActivity(intent);
        });

        // --- KẾT THÚC PHẦN CODE ĐƯỢC THÊM VÀO ---


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}