package com.example.tourbooking.view.home;

import static com.example.tourbooking.adapter.TourAdapter.formatNumber;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.adapter.BannerSliderAdapter;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.view.profile.ProfileActivity;
import com.example.tourbooking.view.profile.SettingsActivity;
import com.example.tourbooking.view.support.ChatSupportActivity;
import com.example.tourbooking.view.support.FeedbackActivity;
import com.example.tourbooking.view.support.NotificationsActivity;
import com.example.tourbooking.view.tour.CompareTourActivity;
import com.example.tourbooking.view.tour.DashBoardActivity;
import com.example.tourbooking.view.tour.ItineraryBuilderActivity;
import com.example.tourbooking.view.tour.SearchActivity;
import com.example.tourbooking.view.tour.SearchResultsActivity;
import com.example.tourbooking.view.tour.TourDetailsActivity;
import com.example.tourbooking.view.tour.TravelInsuranceActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private static final int MAX_IMAGE_SLIDER = 5;
    private EditText etSearch;
    private ViewPager2 bannerSlider;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    private LinearLayout tourContainer, tourRecommendedContainer;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupBannerSlider();
        loadFeaturedTours();
        loadRecommendedTours();
        setupCategoryListeners();
        setupActionButtons();
    }

    private void initializeViews() {
        etSearch = findViewById(R.id.etSearch);
        bannerSlider = findViewById(R.id.bannerSlider);
        tourContainer = findViewById(R.id.tourContainer);
        tourRecommendedContainer = findViewById(R.id.tourRecommendedContainer);

        etSearch.setFocusable(false);
        etSearch.setClickable(true);
        etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    private void setupActionButtons() {
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnSettings = findViewById(R.id.btnSettings);
        ImageButton btnNotifications = findViewById(R.id.btnNotifications);
        ImageButton btnChatSupport = findViewById(R.id.btnChatSupport);
        ImageButton btnFeedback = findViewById(R.id.btnFeedback);
        ImageButton btnDasboard = findViewById(R.id.btnDashboard);
        ImageButton btnCompareTour = findViewById(R.id.btnCompareTour);
        ImageButton btnInsurance = findViewById(R.id.btnInsurance);

        ImageButton btnBuilder = findViewById(R.id.btnBuilder);

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        btnNotifications.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        btnChatSupport.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatSupportActivity.class));
        });

        btnFeedback.setOnClickListener(v -> {
            startActivity(new Intent(this, FeedbackActivity.class));
        });
        btnDasboard.setOnClickListener(v -> {
            startActivity(new Intent(this, DashBoardActivity.class));
        });
        btnCompareTour.setOnClickListener(v -> {
            startActivity(new Intent(this, CompareTourActivity.class));
        });
        btnInsurance.setOnClickListener(v -> {
            startActivity(new Intent(this, TravelInsuranceActivity.class));
        });
        btnBuilder.setOnClickListener(v -> {
            startActivity(new Intent(this, ItineraryBuilderActivity.class));
        });
    }

    private void setupBannerSlider() {
        List<String> imageUrls = new ArrayList<>();
        db.collection("tours").limit(MAX_IMAGE_SLIDER).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String thumbnailUrl = document.getString("thumbnailUrl");
                    if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                        imageUrls.add(thumbnailUrl);
                    }
                }
                BannerSliderAdapter adapter = new BannerSliderAdapter(this, imageUrls);
                bannerSlider.setAdapter(adapter);
                if (!imageUrls.isEmpty()) {
                    startAutoSlider(imageUrls.size());
                }
            }
        });
    }

    private void startAutoSlider(int itemCount) {
        sliderRunnable = () -> {
            int nextItem = (bannerSlider.getCurrentItem() + 1) % itemCount;
            bannerSlider.setCurrentItem(nextItem, true);
        };
        bannerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    sliderHandler.removeCallbacks(sliderRunnable);
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }
            }
        });
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    private void loadFeaturedTours() {
        db.collection("tours")
                .orderBy("rating", Query.Direction.DESCENDING) // Sửa lại, có thể bạn muốn orderBy rating
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tourContainer.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Tour tour = doc.toObject(Tour.class);
                        tour.setId(doc.getId());

                        View tourView = inflater.inflate(R.layout.item_tour, tourContainer, false);
                        ImageView imgTour = tourView.findViewById(R.id.imgTour);
                        TextView txtTourName = tourView.findViewById(R.id.txtTourName);
                        TextView txtTourPrice = tourView.findViewById(R.id.txtTourPrice);

                        txtTourName.setText(tour.getTourName());
                        txtTourPrice.setText(formatNumber(tour.getPrice()));

                        Glide.with(this).load(tour.getThumbnailUrl()).into(imgTour);

                        tourView.setOnClickListener(v -> {
                            Intent intent = new Intent(this, TourDetailsActivity.class);
                            intent.putExtra("tour", tour);
                            intent.putExtra("tour_id", tour.getId());
                            startActivity(intent);
                        });

                        tourContainer.addView(tourView);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load tours", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadRecommendedTours() {
        db.collection("tours")
                .orderBy("price", Query.Direction.ASCENDING) // Ví dụ: tour giá rẻ
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tourRecommendedContainer.removeAllViews();
                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Tour tour = doc.toObject(Tour.class);
                        tour.setId(doc.getId());

                        View tourView = inflater.inflate(R.layout.item_tour, tourRecommendedContainer, false);
                        ImageView imgTour = tourView.findViewById(R.id.imgTour);
                        TextView txtTourName = tourView.findViewById(R.id.txtTourName);
                        TextView txtTourPrice = tourView.findViewById(R.id.txtTourPrice);

                        txtTourName.setText(tour.getTourName());
                        txtTourPrice.setText(formatNumber(tour.getPrice()));

                        Glide.with(this).load(tour.getThumbnailUrl()).into(imgTour);

                        tourView.setOnClickListener(v -> {
                            Intent intent = new Intent(this, TourDetailsActivity.class);
                            intent.putExtra("tour", tour);
                            intent.putExtra("tour_id", tour.getId());
                            startActivity(intent);
                        });

                        tourRecommendedContainer.addView(tourView);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load recommended tours", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupCategoryListeners() {
        findViewById(R.id.mnFiwoEAG22FnSUsuvsS).setOnClickListener(v -> openCategory("mnFiwoEAG22FnSUsuvsS"));
        findViewById(R.id.kKFoXT9xY6kL2euVPni3).setOnClickListener(v -> openCategory("kKFoXT9xY6kL2euVPni3"));
        findViewById(R.id.zYBtnxgB0ZKMBjn6N29v).setOnClickListener(v -> openCategory("zYBtnxgB0ZKMBjn6N29v"));
        findViewById(R.id.reoqlOJY6N1r9qArsKv3).setOnClickListener(v -> openCategory("reoqlOJY6N1r9qArsKv3"));
        findViewById(R.id.FPVzh0puB2TNjv7Gi2dK).setOnClickListener(v -> openCategory("FPVzh0puB2TNjv7Gi2dK"));
    }

    private void openCategory(String categoryId) {
        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
        intent.putExtra("category", categoryId);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderRunnable != null && bannerSlider.getAdapter() != null && bannerSlider.getAdapter().getItemCount() > 0) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }
}