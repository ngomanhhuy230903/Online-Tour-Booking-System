package com.example.tourbooking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar ratingPicker;
    private EditText feedbackTextInput;
    private Button attachScreenshotButton, submitButton, cancelButton;
    private TextView charCountIndicator, successMessage, errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.feedback), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ratingPicker = findViewById(R.id.rating_picker);
        feedbackTextInput = findViewById(R.id.feedback_text_input);
        attachScreenshotButton = findViewById(R.id.attach_screenshot_button);
        submitButton = findViewById(R.id.submit_button);
        cancelButton = findViewById(R.id.cancel_button);
        charCountIndicator = findViewById(R.id.char_count_indicator);
        successMessage = findViewById(R.id.success_message);
        errorMessage = findViewById(R.id.error_message);

        feedbackTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                charCountIndicator.setText(s.length() + "/500");
            }
        });

        attachScreenshotButton.setOnClickListener(v -> {
            // Implement screenshot picker
        });

        submitButton.setOnClickListener(v -> {
            if (ratingPicker.getRating() == 0 || feedbackTextInput.getText().toString().trim().isEmpty()) {
                errorMessage.setVisibility(View.VISIBLE);
                successMessage.setVisibility(View.GONE);
            } else {
                errorMessage.setVisibility(View.GONE);
                successMessage.setVisibility(View.VISIBLE);
                feedbackTextInput.setText("");
                ratingPicker.setRating(0);
            }
        });

        cancelButton.setOnClickListener(v -> finish());
    }
}