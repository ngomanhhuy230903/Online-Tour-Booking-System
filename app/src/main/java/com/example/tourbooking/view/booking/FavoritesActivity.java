package com.example.tourbooking.view.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourbooking.R;
import com.example.tourbooking.adapter.FavoriteAdapter;
import com.example.tourbooking.model.Tour;
import com.example.tourbooking.utils.SessionManager;
import com.example.tourbooking.view.home.HomeActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavoriteTours;
    private FavoriteAdapter adapter;
    private List<Tour> favoriteToursList;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar_favorites);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        initializeViews();
        setupRecyclerView();
        loadFavoriteTours();
    }

    private void initializeViews() {
        rvFavoriteTours = findViewById(R.id.rvFavoriteTours);
        progressBar = findViewById(R.id.progressBarFavorites);
        emptyStateLayout = findViewById(R.id.layoutEmptyState);
        findViewById(R.id.btnGoToHome).setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }

    private void setupRecyclerView() {
        favoriteToursList = new ArrayList<>();
        adapter = new FavoriteAdapter(favoriteToursList, this, tour -> {
            removeFavorite(tour);
        });
        rvFavoriteTours.setLayoutManager(new LinearLayoutManager(this));
        rvFavoriteTours.setAdapter(adapter);
    }

    private void loadFavoriteTours() {
        progressBar.setVisibility(View.VISIBLE);
        rvFavoriteTours.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        String userId = sessionManager.getUserId();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem mục Yêu thích.", Toast.LENGTH_SHORT).show();
            showEmptyState();
            return;
        }

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(favoriteSnapshots -> {
                    if (favoriteSnapshots.isEmpty()) {
                        showEmptyState();
                        return;
                    }
                    List<String> tourIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : favoriteSnapshots) {
                        if (doc.getString("tourId") != null) {
                            tourIds.add(doc.getString("tourId"));
                        }
                    }
                    if (tourIds.isEmpty()) {
                        showEmptyState();
                    } else {
                        fetchToursByIds(tourIds);
                    }
                })
                .addOnFailureListener(e -> showErrorState());
    }

    private void fetchToursByIds(List<String> tourIds) {
        db.collection("tours")
                .whereIn(FieldPath.documentId(), tourIds)
                .get()
                .addOnSuccessListener(tourSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    favoriteToursList.clear();
                    for (DocumentSnapshot doc : tourSnapshots) {
                        Tour tour = doc.toObject(Tour.class);
                        if (tour != null) {
                            tour.setId(doc.getId());
                            favoriteToursList.add(tour);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    if (favoriteToursList.isEmpty()) {
                        showEmptyState();
                    } else {
                        rvFavoriteTours.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> showErrorState());
    }

    private void removeFavorite(Tour tourToRemove) {
        String userId = sessionManager.getUserId();
        if (userId == null) return;

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("tourId", tourToRemove.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            doc.getReference().delete();
                        }
                        loadFavoriteTours();
                        Toast.makeText(this, "Đã xóa khỏi Yêu thích", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEmptyState() {
        progressBar.setVisibility(View.GONE);
        rvFavoriteTours.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.VISIBLE);
    }

    private void showErrorState() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
        emptyStateLayout.setVisibility(View.VISIBLE);
    }
}