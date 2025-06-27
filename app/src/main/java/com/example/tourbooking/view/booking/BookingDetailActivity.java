// File: view/booking/BookingDetailActivity.java
package com.example.tourbooking.view.booking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tourbooking.R;
import com.example.tourbooking.model.Booking;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    public static final String BOOKING_ID_EXTRA = "BOOKING_ID_EXTRA";

    private FirebaseFirestore db;
    private String bookingId;

    private ProgressBar progressBarDetail;
    private LinearLayout contentLayout;
    private TextView tvDetailTourName, tvDetailBookingCode, tvDetailTravelDates, tvDetailGuestList, tvDetailTotalPrice, tvDetailStatus;
    private Button btnDownloadInvoice, btnCancelBooking;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        Toolbar toolbar = findViewById(R.id.toolbar_booking_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        bookingId = getIntent().getStringExtra(BOOKING_ID_EXTRA);
        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        initializeViews();
        fetchBookingDetails();
    }

    private void initializeViews() {
        progressBarDetail = findViewById(R.id.progressBarDetail);
        contentLayout = findViewById(R.id.contentLayout);
        tvDetailTourName = findViewById(R.id.tvDetailTourName);
        tvDetailBookingCode = findViewById(R.id.tvDetailBookingCode);
        tvDetailTravelDates = findViewById(R.id.tvDetailTravelDates);
        tvDetailGuestList = findViewById(R.id.tvDetailGuestList);
        tvDetailTotalPrice = findViewById(R.id.tvDetailTotalPrice);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        btnDownloadInvoice = findViewById(R.id.btnDownloadInvoice);
        btnCancelBooking = findViewById(R.id.btnCancelBooking);
    }

    private void fetchBookingDetails() {
        contentLayout.setVisibility(View.GONE);
        progressBarDetail.setVisibility(View.VISIBLE);

        DocumentReference bookingRef = db.collection("bookings").document(bookingId);
        bookingRef.get().addOnSuccessListener(documentSnapshot -> {
            progressBarDetail.setVisibility(View.GONE);
            if (documentSnapshot.exists()) {
                Booking booking = documentSnapshot.toObject(Booking.class);
                if (booking != null) {
                    populateUi(booking);
                    contentLayout.setVisibility(View.VISIBLE);
                } else {
                    showErrorAndFinish();
                }
            } else {
                showErrorAndFinish();
            }
        }).addOnFailureListener(e -> {
            progressBarDetail.setVisibility(View.GONE);
            showErrorAndFinish();
        });
    }

    private void populateUi(Booking booking) {
        tvDetailTourName.setText(booking.getTourName());
        tvDetailBookingCode.setText("Mã đơn hàng: #" + booking.getId().substring(booking.getId().length() - 7).toUpperCase());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDetailTravelDates.setText("Ngày đặt: " + sdf.format(booking.getBookingDate()));
        // TODO: Cập nhật với dữ liệu thật về số khách
        tvDetailGuestList.setText("Số khách: 2 người lớn");

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvDetailTotalPrice.setText("Tổng tiền: " + currencyFormatter.format(booking.getTotalPrice()));

        tvDetailStatus.setText("Trạng thái: " + booking.getStatus().toUpperCase());

        // Chỉ hiển thị nút Hủy nếu trạng thái là "UPCOMING"
        if ("UPCOMING".equalsIgnoreCase(booking.getStatus())) {
            btnCancelBooking.setVisibility(View.VISIBLE);
            btnCancelBooking.setOnClickListener(v -> {
                // TODO: Thêm dialog hỏi xác nhận trước khi hủy
                cancelBookingInDetail(booking);
            });
        } else {
            btnCancelBooking.setVisibility(View.GONE);
        }
    }

    private void cancelBookingInDetail(Booking booking) {
        btnCancelBooking.setEnabled(false);
        db.collection("bookings").document(booking.getId()).update("status", "CANCELED")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                    booking.setStatus("CANCELED");
                    populateUi(booking); // Cập nhật lại UI
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: Không thể hủy đơn hàng.", Toast.LENGTH_SHORT).show();
                    btnCancelBooking.setEnabled(true);
                });
    }

    private void showErrorAndFinish() {
        Toast.makeText(this, "Không thể tải chi tiết đơn hàng.", Toast.LENGTH_SHORT).show();
        finish();
    }
}