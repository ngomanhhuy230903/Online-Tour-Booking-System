package com.example.tourbooking.view.tour;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.model.ItineraryItem;
import com.example.tourbooking.model.Review;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.utils.SessionManager;
import com.example.tourbooking.view.booking.BookingCalendarActivity;
import com.example.tourbooking.view.booking.FavoritesActivity;
import com.example.tourbooking.view.review.ReviewsListActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TourDetailsActivity extends AppCompatActivity {
    private ImageView ivMainImage;
    private TextView tvTitle, tvTourName, tvDescription, tvItinerary, tvPrices, ratingNumber, tvTotalReviewsClickable;
    private TextView tvIncluded, tvExcluded;
    private RatingBar ratingBar;
    private Button btnBookNow, btnViewMap;
    private ImageButton btnFavorite;
    private LinearLayout galleryContainer, layoutReviews;
    private FirebaseFirestore db;
    private SessionManager sessionManager;
    private boolean isFavorited = false;
    private String favoriteDocumentId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);

        initializeViews();
        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        Tour tour = (Tour) getIntent().getSerializableExtra("tour");
        String tourId = getIntent().getStringExtra("tour_id");

        if (tour != null) {
            tour.setId(tourId); // Gán ID vào đối tượng tour
            loadTourData(tour);

            if (tourId != null) {
                fetchAndDisplayReviewsSummary(tourId);
                checkIfFavorite(tourId);
                setupClickListeners(tour);
            }
        } else {
            Toast.makeText(this, "Lỗi: Dữ liệu tour không hợp lệ.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeViews() {
        galleryContainer = findViewById(R.id.galleryContainer);
        ivMainImage = findViewById(R.id.ivMainImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvTourName = findViewById(R.id.tvTourName);
        tvDescription = findViewById(R.id.tvDescription);
        tvItinerary = findViewById(R.id.tvItinerary);
        tvPrices = findViewById(R.id.tvPrices);
        tvIncluded = findViewById(R.id.tvIncluded);
        tvExcluded = findViewById(R.id.tvExcluded);
        ratingNumber = findViewById(R.id.ratingNumber);
        ratingBar = findViewById(R.id.ratingBar);
        btnBookNow = findViewById(R.id.btnBookNow);
        btnViewMap = findViewById(R.id.btnViewMap);
        layoutReviews = findViewById(R.id.layoutReviews);
        tvTotalReviewsClickable = findViewById(R.id.tvTotalReviewsClickable);
        btnFavorite = findViewById(R.id.btnFavorite);
    }

    private void loadTourData(Tour tour) {
        Glide.with(this).load(tour.getThumbnailUrl()).into(ivMainImage);
        ivMainImage.setOnClickListener(v -> showImageZoom(tour.getThumbnailUrl()));

        if (tour.getImageGallery() != null && !tour.getImageGallery().isEmpty()) {
            galleryContainer.removeAllViews();
            for (String imageUrl : tour.getImageGallery()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(60), dpToPx(40));
                params.setMargins(0, 0, 0, dpToPx(6));
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(v -> showImageZoom(imageUrl));
                Glide.with(this).load(imageUrl).into(imageView);
                galleryContainer.addView(imageView);
            }
        }

        tvTourName.setText(tour.getTourName());
        if (tour.getDescription() != null) {
            tvDescription.setText(tour.getDescription());
        }

        if (tour.getItinerary() != null && !tour.getItinerary().isEmpty()) {
            StringBuilder itineraryBuilder = new StringBuilder();
            for (ItineraryItem item : tour.getItinerary()) {
                itineraryBuilder.append("• ").append(item.getName()).append("\n");
            }
            tvItinerary.setText(itineraryBuilder.toString().trim());
        } else {
            tvItinerary.setText("No itinerary available.");
        }

        String priceText = "Base Price: $" + (tour.getBasePrice() != null ? tour.getBasePrice() : "")
                + "   Taxes: $" + (tour.getTaxes() != null ? tour.getTaxes() : "")
                + "   Fees: $" + (tour.getFees() != null ? tour.getFees() : "");
        tvPrices.setText(priceText);

        if (tour.getIncludedServices() != null && !tour.getIncludedServices().isEmpty()) {
            tvIncluded.setText(TextUtils.join("\n• ", tour.getIncludedServices()));
        }

        if (tour.getExcludedServices() != null && !tour.getExcludedServices().isEmpty()) {
            tvExcluded.setText(TextUtils.join("\n• ", tour.getExcludedServices()));
        }
    }

    private void fetchAndDisplayReviewsSummary(String tourId) {
        db.collection("reviews").whereEqualTo("tourId", tourId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().isEmpty()) {
                            ratingNumber.setText("Mới");
                            tvTotalReviewsClickable.setText("(Chưa có đánh giá)");
                            ratingBar.setRating(0);
                        } else {
                            float totalRating = 0;
                            int reviewCount = task.getResult().size();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Review review = document.toObject(Review.class);
                                totalRating += review.getRating();
                            }
                            float averageRating = totalRating / reviewCount;
                            ratingNumber.setText(String.format(Locale.US, "%.1f", averageRating));
                            ratingBar.setRating(averageRating);
                            tvTotalReviewsClickable.setText("(" + reviewCount + " đánh giá)");
                        }
                    }
                });
    }

    private void setupClickListeners(Tour tour) {
        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(TourDetailsActivity.this, BookingCalendarActivity.class);
            intent.putExtra("tour", tour);
            startActivity(intent);
        });

        layoutReviews.setOnClickListener(v -> {
            Intent intent = new Intent(TourDetailsActivity.this, ReviewsListActivity.class);
            intent.putExtra("TOUR_ID_EXTRA", tour.getId());
            startActivity(intent);
        });

        btnViewMap.setOnClickListener(v -> {
            Intent intent = new Intent(TourDetailsActivity.this, MapTourActivity.class);
            intent.putExtra("tourName", tour.getTourName());
            intent.putExtra("itinerary", new Gson().toJson(tour.getItinerary()));
            startActivity(intent);
        });

        btnFavorite.setOnClickListener(v -> toggleFavorite(tour.getId()));
    }

    private void checkIfFavorite(String tourId) {
        String userId = sessionManager.getUserId();
        if (userId == null) return;
        db.collection("favorites").whereEqualTo("userId", userId).whereEqualTo("tourId", tourId).limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        isFavorited = true;
                        favoriteDocumentId = task.getResult().getDocuments().get(0).getId();
                    } else {
                        isFavorited = false;
                        favoriteDocumentId = null;
                    }
                    updateFavoriteButtonUI();
                });
    }

    private void toggleFavorite(String tourId) {
        if (isFavorited) {
            removeFromFavorites();
        } else {
            addToFavorites(tourId);
        }
    }

    private void addToFavorites(String tourId) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để yêu thích.", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("userId", userId);
        favoriteData.put("tourId", tourId);
        favoriteData.put("timestamp", FieldValue.serverTimestamp());
        db.collection("favorites").add(favoriteData).addOnSuccessListener(docRef -> {
            Toast.makeText(this, "Đã thêm vào Yêu thích", Toast.LENGTH_SHORT).show();
            isFavorited = true;
            favoriteDocumentId = docRef.getId();
            updateFavoriteButtonUI();
        });
    }

    private void removeFromFavorites() {
        if (favoriteDocumentId == null) return;
        db.collection("favorites").document(favoriteDocumentId).delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Đã xóa khỏi Yêu thích", Toast.LENGTH_SHORT).show();
            isFavorited = false;
            favoriteDocumentId = null;
            updateFavoriteButtonUI();
        });
    }

    private void updateFavoriteButtonUI() {
        if (isFavorited) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void showImageZoom(String imageUrl) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_zoom);
        ImageView imgZoom = dialog.findViewById(R.id.imgZoom);
        Glide.with(this).load(imageUrl).into(imgZoom);
        imgZoom.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}