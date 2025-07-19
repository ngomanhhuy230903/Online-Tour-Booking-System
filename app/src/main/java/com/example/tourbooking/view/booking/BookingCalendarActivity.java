package com.example.tourbooking.view.booking;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourbooking.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BookingCalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private TextView tvSelectedDate, tvTotal;
    private EditText tvGuestCount, etSpecialRequests;
    private Spinner spinnerRoomType;
    private Button btnProceed;

    private final double basePrice = 250.0;
    private final DecimalFormat formatter = new DecimalFormat("$###,###.00");

    private long startDate = -1;
    private long endDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_calendar);

        calendarView = findViewById(R.id.calendarView);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvGuestCount = findViewById(R.id.tvGuestCount);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        etSpecialRequests = findViewById(R.id.etSpecialRequests);
        tvTotal = findViewById(R.id.tvTotal);
        btnProceed = findViewById(R.id.btnProceed);

        // Room type adapter
        String[] rooms = {"Standard", "Deluxe", "Suite"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rooms);
        spinnerRoomType.setAdapter(adapter);

        // Calendar: handle range date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            long selectedTime = selected.getTimeInMillis();

            if (startDate == -1 || (startDate != -1 && endDate != -1)) {
                startDate = selectedTime;
                endDate = -1;
                tvSelectedDate.setText("Start Date: " + formatDate(selectedTime));
            } else if (selectedTime > startDate) {
                endDate = selectedTime;
                tvSelectedDate.setText("Selected Range: " + formatDate(startDate) + " → " + formatDate(endDate));
            } else {
                startDate = selectedTime;
                endDate = -1;
                tvSelectedDate.setText("Start Date: " + formatDate(startDate));
            }

            calculateTotal();
        });

        spinnerRoomType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {
                calculateTotal();
            }

            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnProceed.setOnClickListener(v -> {
            if (startDate == -1) {
                Toast.makeText(this, "Please select at least a start date.", Toast.LENGTH_SHORT).show();
                return;
            }

            int guests = Integer.parseInt(tvGuestCount.getText().toString());
            String roomType = spinnerRoomType.getSelectedItem().toString();
            String request = etSpecialRequests.getText().toString();

            String dateInfo = (endDate != -1) ?
                    "From " + formatDate(startDate) + " to " + formatDate(endDate) :
                    "Date: " + formatDate(startDate);

            Toast.makeText(this,
                    "Booking confirmed!\n" +
                            "Guests: " + guests + "\n" +
                            "Room: " + roomType + "\n" +
                            dateInfo,
                    Toast.LENGTH_LONG).show();
        });

        calculateTotal();
    }

    private void calculateTotal() {
        try {
            int guests = Integer.parseInt(tvGuestCount.getText().toString());
            String room = spinnerRoomType.getSelectedItem().toString();

            int numDays = 1;
            if (startDate != -1 && endDate != -1) {
                long diff = endDate - startDate;
                numDays = (int) (diff / (1000 * 60 * 60 * 24)) + 1;
            }

            double multiplier = 1.0;
            if (room.equals("Deluxe")) multiplier = 1.3;
            else if (room.equals("Suite")) multiplier = 1.6;

            double total = guests * basePrice * multiplier * numDays;
            tvTotal.setText("Estimated Total: " + formatter.format(total));
        } catch (Exception e) {
            tvTotal.setText("Estimated Total: $0.00");
        }
    }

    private String formatDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}
