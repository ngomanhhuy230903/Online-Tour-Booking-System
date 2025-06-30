package com.example.tourbooking.tour;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tourbooking.R;
import com.example.tourbooking.home.HomeActivity;

public class SearchActivity extends AppCompatActivity {
    private ImageView ivBack;
    private EditText etKeyword, etLocation;
    private Spinner spnTourType, spnDuration;
    private SeekBar priceSlider;
    private Button btnDeparture, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivBack = findViewById(R.id.ivBack);
        etKeyword = findViewById(R.id.etKeyword);
        etLocation = findViewById(R.id.etLocation);
        spnTourType = findViewById(R.id.spnTourType);
        spnDuration = findViewById(R.id.spnDuration);
        priceSlider = findViewById(R.id.priceSlider);
        btnDeparture = findViewById(R.id.btnDeparture);
        btnReturn = findViewById(R.id.btnReturn);

        ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
            // Optional: Clear stack nếu muốn HomeActivity là màn duy nhất còn lại
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(v -> {
            etKeyword.setText("");
            etLocation.setText("");
            priceSlider.setProgress(0);
            spnTourType.setSelection(0);
            spnDuration.setSelection(0);
            btnDeparture.setText("Departure Date");
            btnReturn.setText("Return Date");
        });
    }
}