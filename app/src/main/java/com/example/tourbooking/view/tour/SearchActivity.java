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
import com.example.tourbooking.view.home.HomeActivity;
import com.google.android.material.slider.RangeSlider;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    EditText etKeyword, etLocation;
    Button btnDepartureDate, btnReturnDate, btnSearch, btnClearFilters;
    Spinner spinnerTourType, spinnerDuration;
    RangeSlider priceSlider;
    Calendar departureCalendar = Calendar.getInstance();
    Calendar returnCalendar = Calendar.getInstance();

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
        btnDepartureDate.setOnClickListener(v -> showDatePicker(departureCalendar, btnDepartureDate));
        btnReturnDate.setOnClickListener(v -> showDatePicker(returnCalendar, btnReturnDate));

        // Search button
        btnSearch.setOnClickListener(v -> {
            String keyword = etKeyword.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String type = spinnerTourType.getSelectedItem().toString();
            String duration = spinnerDuration.getSelectedItem().toString();

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
    }

    private void showDatePicker(Calendar calendar, Button targetBtn) {
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    targetBtn.setText((month + 1) + "/" + day + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}