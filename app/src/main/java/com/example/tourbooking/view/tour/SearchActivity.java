package com.example.tourbooking.view.tour;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tourbooking.R;
import com.example.tourbooking.model.ItineraryItem;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.view.home.HomeActivity;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {
    EditText etKeyword, etLocation;
    Button btnDepartureDate, btnReturnDate, btnSearch, btnClearFilters;
    Spinner spinnerTourType, spinnerDuration;
    RangeSlider priceSlider;
    Calendar departureCalendar = null;
    Calendar returnCalendar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etKeyword = findViewById(R.id.etKeyword);
        etLocation = findViewById(R.id.etLocation);
        btnDepartureDate = findViewById(R.id.btnDepartureDate);
        btnReturnDate = findViewById(R.id.btnReturnDate);
        btnSearch = findViewById(R.id.btnSearch);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        spinnerTourType = findViewById(R.id.spinnerTourType);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        priceSlider = findViewById(R.id.priceRangeSlider);

        // Tour Type List (with default label)
        List<String> tourTypes = Arrays.asList("Tour Type", "Adventure", "Cultural", "Relaxation", "Nature");
        ArrayAdapter<String> tourTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tourTypes);
        tourTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTourType.setAdapter(tourTypeAdapter);

        // Duration List (with default label)
        List<String> durations = Arrays.asList("Duration", "1 Day", "2-3 Days", "1 Week", "More than 1 Week");
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);

        // Date Pickers
        btnDepartureDate.setOnClickListener(v -> showDatePicker("departure"));
        btnReturnDate.setOnClickListener(v -> showDatePicker("return"));

        // Search button
        btnSearch.setOnClickListener(v -> {
            String keyword = etKeyword.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String type = spinnerTourType.getSelectedItem().toString();
            String duration = spinnerDuration.getSelectedItem().toString();
            Long departureDate = departureCalendar != null ? departureCalendar.getTimeInMillis() : null;
            Long returnDate = returnCalendar != null ? returnCalendar.getTimeInMillis() : null;

            if (type == "Tour Type") {
                type = null;
            }
            if (duration == "Duration") {
                duration = null;
            }

            List<Float> priceValues = priceSlider.getValues();
            float minPrice = priceValues.get(0);
            float maxPrice = priceValues.get(1);

            // Gửi sang SearchResultsActivity
            Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
            intent.putExtra("keyword", keyword);
            intent.putExtra("location", location);
            intent.putExtra("type", type);
            intent.putExtra("duration", duration);
            intent.putExtra("minPrice", minPrice);
            intent.putExtra("maxPrice", maxPrice);
            intent.putExtra("departureDate", departureDate);
            intent.putExtra("returnDate", returnDate);
            startActivity(intent);
        });

        // Clear filters
        btnClearFilters.setOnClickListener(v -> {
            etKeyword.setText("");
            etLocation.setText("");
            spinnerTourType.setSelection(0);
            spinnerDuration.setSelection(0);
            btnDepartureDate.setText("Departure Date");
            btnReturnDate.setText("Return Date");
        });

//        fixItineraryTimeAndTourTime();
    }

    private void showDatePicker(String type) {
        Calendar tempCalendar;

        // Dùng current day nếu calendar đang null
        if (type.equals("departure")) {
            tempCalendar = departureCalendar != null ? departureCalendar : Calendar.getInstance();
        } else {
            tempCalendar = returnCalendar != null ? returnCalendar : Calendar.getInstance();
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, day);

                    if (type.equals("departure")) {
                        departureCalendar = selected;
                        btnDepartureDate.setText((month + 1) + "/" + day + "/" + year);
                    } else {
                        returnCalendar = selected;
                        btnReturnDate.setText((month + 1) + "/" + day + "/" + year);
                    }
                },
                tempCalendar.get(Calendar.YEAR),
                tempCalendar.get(Calendar.MONTH),
                tempCalendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void fixItineraryTimeAndTourTime() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<String> tourNames = Arrays.asList(
                "Chinh Phục Fansipan", "Khám Phá Hà Giang", "Về Miền Tây Sông Nước",
                "Đà Lạt Sương Mù", "Hạ Long Kỳ Quan", "Đảo Phú Quốc", "Vịnh Cam Ranh",
                "Tây Nguyên Huyền Bí", "Sapa Mùa Hoa", "Tràng An Hùng Vĩ",
                "Cố Đô Huế", "Hội An Cổ Kính", "Đà Nẵng Hiện Đại", "Biển Nha Trang",
                "Đảo Cô Tô", "Thác Bản Giốc", "Chùa Bái Đính", "Côn Đảo Kỳ Bí",
                "Hang Sơn Đoòng", "Rừng U Minh"
        );

        Random random = new Random();

        db.collection("tours").get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        String tourId = doc.getId();
                        List<Map<String, Object>> itinerary = (List<Map<String, Object>>) doc.get("itinerary");

                        if (itinerary == null || itinerary.isEmpty()) continue;

                        for (int i = 0; i < itinerary.size(); i++) {
                            String randomLocation = tourNames.get(random.nextInt(tourNames.size()));
                            Map<String, Object> item = itinerary.get(i);
                            item.put("location", randomLocation);
                        }

                        // Tạo map cập nhật
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("itinerary", itinerary);

                        db.collection("tours").document(tourId)
                                .update(updateData)
                                .addOnSuccessListener(aVoid -> Log.d("UPDATE", "Tour updated: " + tourId))
                                .addOnFailureListener(e -> Log.e("UPDATE", "Failed to update tour: " + tourId, e));
                    }
                })
                .addOnFailureListener(e -> Log.e("FIRESTORE", "Failed to fetch tours", e));
    }
}