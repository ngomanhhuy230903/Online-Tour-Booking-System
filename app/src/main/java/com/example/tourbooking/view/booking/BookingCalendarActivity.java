package com.example.tourbooking.view.booking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.R;
import com.example.tourbooking.model.Tour;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class BookingCalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView tvSelectedDate, tvTotal;
    private EditText etGuestCount, etSpecialRequests;
    private Spinner spinnerRoomType; // Khai báo lại
    private Button btnProceed;

    private Tour currentTour;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private long selectedDateTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_calendar);

        currentTour = (Tour) getIntent().getSerializableExtra("tour");
        if (currentTour == null) {
            Toast.makeText(this, "Lỗi: Không có thông tin tour.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupRoomTypeSpinner(); // Gọi hàm cài đặt Spinner
        setupListeners();
        calculateTotal();
    }

    private void initializeViews() {
        calendarView = findViewById(R.id.calendarView);
        calendarView.setMinDate(System.currentTimeMillis() - 1000);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        etGuestCount = findViewById(R.id.etGuestCount);
        tvTotal = findViewById(R.id.tvTotal);
        btnProceed = findViewById(R.id.btnProceed);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        etSpecialRequests = findViewById(R.id.etSpecialRequests);
    }

    // Hàm mới để cài đặt Spinner
    private void setupRoomTypeSpinner() {
        String[] rooms = {"Phòng Tiêu chuẩn", "Phòng Deluxe", "Phòng Suite"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rooms);
        spinnerRoomType.setAdapter(adapter);
    }

    private void setupListeners() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            selectedDateTime = selected.getTimeInMillis();
            tvSelectedDate.setText("Ngày khởi hành đã chọn: " + formatDate(selectedDateTime));
            calculateTotal();
        });

        etGuestCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Thêm listener cho Spinner để tự động cập nhật giá
        spinnerRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                calculateTotal();
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnProceed.setOnClickListener(v -> {
            if (selectedDateTime == -1) {
                Toast.makeText(this, "Vui lòng chọn ngày khởi hành.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etGuestCount.getText().toString().isEmpty() || Integer.parseInt(etGuestCount.getText().toString()) <= 0) {
                Toast.makeText(this, "Vui lòng nhập số khách hợp lệ.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thu thập dữ liệu
            String guestInfo = etGuestCount.getText().toString() + " khách - " + spinnerRoomType.getSelectedItem().toString();
            String selectedDateStr = formatDate(selectedDateTime);
            double totalAmount = calculateTotal();

            // Tạo Intent và gửi dữ liệu thật sang PaymentActivity
            Intent intent = new Intent(BookingCalendarActivity.this, PaymentActivity.class);
            intent.putExtra("TOUR_ID", currentTour.getId());
            intent.putExtra("TOUR_NAME", currentTour.getTourName());
            intent.putExtra("GUEST_COUNT", guestInfo); // Gửi cả thông tin phòng
            intent.putExtra("SELECTED_DATE", selectedDateStr);
            intent.putExtra("TOTAL_AMOUNT", totalAmount);
            startActivity(intent);
        });
    }

    private double calculateTotal() {
        double total = 0.0;
        try {
            int guests = Integer.parseInt(etGuestCount.getText().toString());
            String room = spinnerRoomType.getSelectedItem().toString();

            double multiplier = 1.0;
            if (room.equals("Phòng Deluxe")) multiplier = 1.3;
            else if (room.equals("Phòng Suite")) multiplier = 1.6;

            total = guests * currentTour.getPrice() * multiplier; // Dùng giá thật của tour và nhân với hệ số phòng
            tvTotal.setText("Tổng chi phí ước tính: " + currencyFormatter.format(total));
        } catch (Exception e) {
            tvTotal.setText("Tổng chi phí ước tính: 0 VNĐ");
        }
        return total;
    }

    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}