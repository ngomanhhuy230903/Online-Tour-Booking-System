package com.example.tourbooking.view.tour;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.view.booking.BookingCalendarActivity;
import com.google.gson.Gson;

public class TourDetailsActivity extends AppCompatActivity {
    private ImageView ivMainImage;
    private TextView tvTitle, tvTourName, tvDescription, tvItinerary, tvPrices, ratingNumber;
    private TextView tvIncluded, tvExcluded;
    private RatingBar ratingBar;
    private Button btnBookNow, btnViewMap;
    private LinearLayout galleryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_details);

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

        // Load tour từ intent
        Tour tour = (Tour) getIntent().getSerializableExtra("tour");

        if (tour != null) {
            btnViewMap.setOnClickListener(v -> {
                Intent intent = new Intent(TourDetailsActivity.this, MapTourActivity.class);
                intent.putExtra("tourName", tour.getTourName());
                intent.putExtra("itinerary", new Gson().toJson(tour.getItinerary()));
                startActivity(intent);
            });
            loadTourData(tour);
        }
    }

    private void loadTourData(Tour tour) {
        // Load ảnh chính
        Glide.with(this)
                .load(tour.getThumbnailUrl())
                .into(ivMainImage);
        ivMainImage.setOnClickListener(v -> showImageZoom(tour.getThumbnailUrl()));

        // Load ảnh phụ
        if (tour.getImageGallery() != null && !tour.getImageGallery().isEmpty()) {
            galleryContainer.removeAllViews(); // Xóa ảnh cũ nếu có

            for (String imageUrl : tour.getImageGallery()) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(60), dpToPx(40));
                params.setMargins(0, 0, 0, dpToPx(6)); // marginBottom = 6dp
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(v -> showImageZoom(imageUrl));

                Glide.with(this)
                        .load(imageUrl)
                        .into(imageView);

                galleryContainer.addView(imageView);
            }
        }

        // Set các field cơ bản
        tvTourName.setText(tour.getTourName());
        if (tour.getDescription() != null) {
            tvDescription.setText(tour.getDescription());
        }

        // Set lịch trình
        if (tour.getItinerary() != null && !tour.getItinerary().isEmpty()) {
            StringBuilder itineraryBuilder = new StringBuilder();
            for (int i = 0; i < tour.getItinerary().size(); i++) {
                itineraryBuilder.append("• Day ").append(i + 1).append(": ").append(tour.getItinerary().get(i)).append("\n");
            }
            tvItinerary.setText(itineraryBuilder.toString().trim());
        } else {
            tvItinerary.setText("No itinerary available.");
        }

        // Giá cả
        String priceText = "Base Price: $" + (tour.getBasePrice() != null ? tour.getBasePrice() : "")
                + "   Taxes: $" + (tour.getTaxes() != null ? tour.getTaxes() : "")
                + "   Fees: $" + (tour.getFees() != null ? tour.getFees() : "");
        tvPrices.setText(priceText);

        // Rating
        if (tour.getRating() != null) {
            ratingNumber.setText(String.valueOf(tour.getRating()));
            ratingBar.setRating(tour.getRating());
        }

        // Included Services
        if (tour.getIncludedServices() != null && !tour.getIncludedServices().isEmpty()) {
            tvIncluded.setText(String.join("\n• ", tour.getIncludedServices()));
        }

        // Excluded Services
        if (tour.getExcludedServices() != null && !tour.getExcludedServices().isEmpty()) {
            tvExcluded.setText(String.join("\n• ", tour.getExcludedServices()));
        }

        // BOOK NOW click event
        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(TourDetailsActivity.this, BookingCalendarActivity.class);
            intent.putExtra("tour", tour);
            startActivity(intent);
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void showImageZoom(String imageUrl) {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_zoom);

        ImageView imgZoom = dialog.findViewById(R.id.imgZoom);

        Glide.with(this)
                .load(imageUrl)
                .into(imgZoom);

        imgZoom.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}