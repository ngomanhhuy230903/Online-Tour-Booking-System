// File: view/booking/BookingCalendarActivity.java (Đã cập nhật để kết nối)
package com.example.tourbooking.view.booking;

import android.content.Intent; // Import mới
import android.os.Bundle;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class BookingCalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView tvSelectedDate, tvTotal;
    private EditText etGuestCount, etSpecialRequests; // Đổi tên để rõ ràng hơn
    private Spinner spinnerRoomType;
    private Button btnProceed;

    // Giả sử tour này có giá 1,000,000 VNĐ / khách
    private final double basePrice = 1000000.0;
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private long selectedDateTime = -1; // Chỉ cho chọn 1 ngày

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_calendar);

        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        etGuestCount = findViewById(R.id.etGuestCount); // Dùng ID mới
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        etSpecialRequests = findViewById(R.id.etSpecialRequests);
        tvTotal = findViewById(R.id.tvTotal);
        btnProceed = findViewById(R.id.btnProceed);

        // Room type adapter
        String[] rooms = {"Phòng Tiêu chuẩn", "Phòng Deluxe", "Phòng Suite"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rooms);
        spinnerRoomType.setAdapter(adapter);

        // Chỉ cho phép chọn 1 ngày khởi hành
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            selectedDateTime = selected.getTimeInMillis();

            tvSelectedDate.setText("Ngày khởi hành đã chọn: " + formatDate(selectedDateTime));
            calculateTotal();
        });

        // Thêm listener cho Spinner và EditText để tự động cập nhật giá
        spinnerRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {
                calculateTotal();
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        // Bạn có thể thêm TextWatcher cho etGuestCount để cập nhật giá khi người dùng gõ

        btnProceed.setOnClickListener(v -> {
            if (selectedDateTime == -1) {
                Toast.makeText(this, "Vui lòng chọn ngày khởi hành.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (etGuestCount.getText().toString().isEmpty() || Integer.parseInt(etGuestCount.getText().toString()) <= 0) {
                Toast.makeText(this, "Vui lòng nhập số khách hợp lệ.", Toast.LENGTH_SHORT).show();
                return;
            }

            // === PHẦN KẾT NỐI MÀN HÌNH ===
            // 1. Thu thập dữ liệu
            String guestCount = etGuestCount.getText().toString() + " khách";
            String selectedDate = formatDate(selectedDateTime);
            double totalAmount = calculateTotal();

            // 2. Tạo Intent và đính kèm dữ liệu
            Intent intent = new Intent(BookingCalendarActivity.this, PaymentActivity.class);
            intent.putExtra("TOUR_NAME", "Tour khám phá Vịnh Hạ Long 2 ngày 1 đêm"); // Tên tour (tạm thời)
            intent.putExtra("GUEST_COUNT", guestCount);
            intent.putExtra("SELECTED_DATE", selectedDate);
            intent.putExtra("TOTAL_AMOUNT", totalAmount);

            // 3. Mở màn hình PaymentActivity
            startActivity(intent);
        });

        calculateTotal();
    }

    private double calculateTotal() {
        double total = 0.0;
        try {
            int guests = Integer.parseInt(etGuestCount.getText().toString());
            String room = spinnerRoomType.getSelectedItem().toString();

            double multiplier = 1.0;
            if (room.equals("Phòng Deluxe")) multiplier = 1.3;
            else if (room.equals("Phòng Suite")) multiplier = 1.6;

            total = guests * basePrice * multiplier;
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