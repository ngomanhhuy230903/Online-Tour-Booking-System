package com.example.tourbooking.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.tourbooking.R;
import com.example.tourbooking.adapter.BannerSliderAdapter;
import com.example.tourbooking.model.Category;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.view.tour.SearchActivity;
import com.example.tourbooking.view.tour.SearchResultsActivity;
import com.example.tourbooking.view.tour.TourDetailsActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final int MAX_IMAGE_SLIDER = 5;
    private EditText etSearch;
    private ViewPager2 bannerSlider;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    private LinearLayout tourContainer;
    private LinearLayout beachCategory;
    private LinearLayout mountainCategory;
    private LinearLayout culturalCategory;
    private LinearLayout adventureCategory;
    private LinearLayout specialCategory;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etSearch = findViewById(R.id.etSearch);
        etSearch.setFocusable(false);
        etSearch.setClickable(true);
        etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        bannerSlider = findViewById(R.id.bannerSlider);
        List<String> imageUrls = new ArrayList<>();

        db.collection("tours").limit(MAX_IMAGE_SLIDER).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Tour tour = document.toObject(Tour.class);
                    if (tour != null && tour.getThumbnailUrl() != null && !tour.getThumbnailUrl().isEmpty()) {
                        imageUrls.add(tour.getThumbnailUrl());
                    }
                }
                BannerSliderAdapter adapter = new BannerSliderAdapter(this, imageUrls);
                bannerSlider.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Failed to load banners", Toast.LENGTH_SHORT).show();
            }
        });

        // auto transform slider
        BannerSliderAdapter adapter = new BannerSliderAdapter(this, imageUrls);
        bannerSlider.setAdapter(adapter);
        // Auto-slide sau mỗi 3 giây
        sliderRunnable = () -> {
            int next = bannerSlider.getCurrentItem() + 1;
            if (next >= imageUrls.size()) {
                next = 0; // quay lại ảnh đầu tiên
            }
            bannerSlider.setCurrentItem(next, true);
            sliderHandler.postDelayed(sliderRunnable, 3000); // lặp lại sau 3 giây
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);

        // Pause when user swipes
        bannerSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // reset lại thời gian khi user vuốt
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        tourContainer = findViewById(R.id.tourContainer);
        db.collection("tours")
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    LayoutInflater inflater = LayoutInflater.from(this);

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Tour tour = doc.toObject(Tour.class);

                        // Inflate layout
                        View tourView = inflater.inflate(R.layout.item_tour, tourContainer, false);

                        // Gán dữ liệu
                        ImageView imgTour = tourView.findViewById(R.id.imgTour);
                        TextView txtTourName = tourView.findViewById(R.id.txtTourName);
                        TextView txtTourPrice = tourView.findViewById(R.id.txtTourPrice);

                        txtTourName.setText(tour.getTourName());
                        txtTourPrice.setText("$" + (int) tour.getPrice());

                        // Load ảnh bằng Glide
                        Glide.with(this)
                                .load(tour.getThumbnailUrl())
                                .into(imgTour);

                        tourView.setOnClickListener(v -> {
                            Intent intent = new Intent(this, TourDetailsActivity.class);
                            intent.putExtra("tour", tour); // gửi tour qua intent
                            startActivity(intent);
                        });

                        // Thêm view vào LinearLayout
                        tourContainer.addView(tourView);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load tours", Toast.LENGTH_SHORT).show();
                });

        beachCategory = findViewById(R.id.mnFiwoEAG22FnSUsuvsS);
        mountainCategory = findViewById(R.id.kKFoXT9xY6kL2euVPni3);
        culturalCategory = findViewById(R.id.zYBtnxgB0ZKMBjn6N29v);
        adventureCategory = findViewById(R.id.reoqlOJY6N1r9qArsKv3);
        specialCategory = findViewById(R.id.FPVzh0puB2TNjv7Gi2dK);
        beachCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
            intent.putExtra("category", "mnFiwoEAG22FnSUsuvsS");
            startActivity(intent);
        });
        mountainCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
            intent.putExtra("category", "kKFoXT9xY6kL2euVPni3");
            startActivity(intent);
        });
        culturalCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
            intent.putExtra("category", "zYBtnxgB0ZKMBjn6N29v");
            startActivity(intent);
        });
        adventureCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
            intent.putExtra("category", "reoqlOJY6N1r9qArsKv3");
            startActivity(intent);
        });
        specialCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
            intent.putExtra("category", "FPVzh0puB2TNjv7Gi2dK");
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}