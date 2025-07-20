// File: view/booking/PaymentConfirmationActivity.java
package com.example.tourbooking.view.booking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.MainActivity;
import com.example.tourbooking.R;
import com.example.tourbooking.view.home.HomeActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PaymentConfirmationActivity extends AppCompatActivity {

    public static final String EXTRA_BOOKING_ID = "EXTRA_BOOKING_ID";
    public static final String EXTRA_TOUR_NAME = "EXTRA_TOUR_NAME";
    public static final String EXTRA_BOOKING_DATE = "EXTRA_BOOKING_DATE";

    private TextView tvConfirmationCode, tvBookingSummary;
    private ImageView ivQrCode;
    private Button btnDownloadItinerary, btnShare, btnViewBookings, btnGoHome;

    private String bookingId;
    private String tourName;
    private String bookingDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        initializeViews();

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        bookingId = intent.getStringExtra(EXTRA_BOOKING_ID);
        tourName = intent.getStringExtra(EXTRA_TOUR_NAME);
        bookingDate = intent.getStringExtra(EXTRA_BOOKING_DATE);

        populateUI();
        setupClickListeners();
    }

    private void initializeViews() {
        tvConfirmationCode = findViewById(R.id.tvConfirmationCode);
        tvBookingSummary = findViewById(R.id.tvBookingSummary);
        ivQrCode = findViewById(R.id.ivQrCode);
        btnDownloadItinerary = findViewById(R.id.btnDownloadItinerary);
        btnShare = findViewById(R.id.btnShare);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnGoHome = findViewById(R.id.btnGoHome);
    }

    private void populateUI() {
        if (bookingId != null) {
            tvConfirmationCode.setText("Mã đơn hàng: #" + bookingId.substring(bookingId.length() - 7).toUpperCase());
            generateQrCode(bookingId);
        }
        tvBookingSummary.setText("Tour: " + tourName + "\nNgày đi: " + bookingDate);
    }

    private void generateQrCode(String text) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tạo mã QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        btnDownloadItinerary.setOnClickListener(v -> {
            // TODO: Triển khai logic tải file PDF
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });

        btnShare.setOnClickListener(v -> shareBookingDetails());

        btnViewBookings.setOnClickListener(v -> {
            // TODO: Mở BookingHistoryActivity (M17)
             Intent intent = new Intent(this, BookingHistoryActivity.class);
             startActivity(intent);
        });

        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void shareBookingDetails() {
        String shareText = "Tôi đã đặt tour thành công!\n" +
                "Mã đơn hàng: " + bookingId + "\n" +
                "Tên tour: " + tourName + "\n" +
                "Ngày đi: " + bookingDate;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ đơn hàng qua"));
    }
}