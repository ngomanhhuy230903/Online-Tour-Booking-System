package com.example.tourbooking.view.review;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.ReviewAdapter;
import com.example.tourbooking.model.Review;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReviewsListActivity extends AppCompatActivity {

    private String tourId;
    private FirebaseFirestore db;

    private RecyclerView rvReviews;
    private ReviewAdapter adapter;
    private List<Review> reviewList;
    private ProgressBar progressBar;
    private TextView tvNoReviews, tvAverageRating, tvTotalReviews;
    private RatingBar rbAverageRating;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews_list);

        // Lấy tourId được truyền từ màn hình trước (MainActivity)
        tourId = getIntent().getStringExtra("TOUR_ID_EXTRA");
        if (tourId == null || tourId.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin tour.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        initializeViews();
        setupRecyclerView();
        loadReviews();
    }

    private void initializeViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_reviews);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        rvReviews = findViewById(R.id.rvReviews);
        progressBar = findViewById(R.id.progressBarReviews);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvTotalReviews = findViewById(R.id.tvTotalReviews);
        rbAverageRating = findViewById(R.id.rbAverageRating);
    }

    private void setupRecyclerView() {
        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(adapter);
    }

    private void loadReviews() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoReviews.setVisibility(View.GONE);
        rvReviews.setVisibility(View.GONE);

        db.collection("reviews")
                .whereEqualTo("tourId", tourId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        reviewList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            reviewList.add(document.toObject(Review.class));
                        }

                        if(reviewList.isEmpty()) {
                            tvNoReviews.setVisibility(View.VISIBLE);
                        } else {
                            rvReviews.setVisibility(View.VISIBLE);
                            adapter.setReviewList(reviewList);
                            updateRatingSummary();
                        }
                    } else {
                        // Lỗi này thường do thiếu Index
                        tvNoReviews.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Lỗi khi tải đánh giá. Vui lòng kiểm tra Index trên Firebase.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateRatingSummary() {
        if (reviewList.isEmpty()) return;

        float totalRating = 0;
        for (Review review : reviewList) {
            totalRating += review.rating;
        }
        float average = totalRating / reviewList.size();

        tvAverageRating.setText(String.format(Locale.US, "%.1f", average));
        rbAverageRating.setRating(average);
        tvTotalReviews.setText("Từ " + reviewList.size() + " đánh giá");
    }
}