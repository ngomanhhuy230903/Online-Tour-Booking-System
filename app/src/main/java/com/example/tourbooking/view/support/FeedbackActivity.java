package com.example.tourbooking.view.support;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Feedback;
import com.example.tourbooking.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar feedbackRatingBar;
    private TextInputEditText etFeedbackText;
    private Button btnAttachScreenshot, btnSubmitFeedback;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_feedback);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        feedbackRatingBar = findViewById(R.id.feedbackRatingBar);
        etFeedbackText = findViewById(R.id.etFeedbackText);
        btnAttachScreenshot = findViewById(R.id.btnAttachScreenshot);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);
        progressBar = findViewById(R.id.progressBarFeedback);
    }

    private void setupClickListeners() {
        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
        btnAttachScreenshot.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đính kèm ảnh đang phát triển.", Toast.LENGTH_SHORT).show();
        });
    }

    private void submitFeedback() {
        float rating = feedbackRatingBar.getRating();
        String feedbackText = etFeedbackText.getText().toString().trim();

        if (rating == 0 && TextUtils.isEmpty(feedbackText)) {
            Toast.makeText(this, "Vui lòng cho điểm hoặc nhập phản hồi.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);

        String userId = sessionManager.getUserId();
        if (userId == null) {
            userId = "GUEST_USER"; // Dành cho người dùng chưa đăng nhập
        }

        Feedback feedback = new Feedback();
        feedback.userId = userId;
        feedback.rating = rating;
        feedback.feedbackText = feedbackText;
        feedback.screenshotUrl = null; // Sẽ cập nhật sau

        db.collection("feedbacks")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Cảm ơn bạn đã gửi phản hồi!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(this, "Lỗi: Không thể gửi phản hồi.", Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSubmitFeedback.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSubmitFeedback.setEnabled(true);
        }
    }
}