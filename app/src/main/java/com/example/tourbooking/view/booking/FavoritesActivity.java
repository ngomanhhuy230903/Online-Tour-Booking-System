package com.example.tourbooking.view.booking;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView rvFavoriteTours;
    private FavoriteAdapter adapter;
    private List<Tour> favoriteToursList;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        Toolbar toolbar = findViewById(R.id.toolbar_favorites);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupRecyclerView();
        loadFavoriteTours();
    }

    private void initializeViews() {
        rvFavoriteTours = findViewById(R.id.rvFavoriteTours);
        progressBar = findViewById(R.id.progressBarFavorites);
        emptyStateLayout = findViewById(R.id.layoutEmptyState);
        findViewById(R.id.btnGoToHome).setOnClickListener(v -> finish()); // Đóng màn hình này
    }

    private void setupRecyclerView() {
        favoriteToursList = new ArrayList<>();
        adapter = new FavoriteAdapter(favoriteToursList, this, position -> {
            removeFavorite(position);
        });
        rvFavoriteTours.setLayoutManager(new LinearLayoutManager(this));
        rvFavoriteTours.setAdapter(adapter);
    }

    private void loadFavoriteTours() {
        progressBar.setVisibility(View.VISIBLE);
        rvFavoriteTours.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        String userId = "DUMMY_USER_ID_123"; // Dùng ID test
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        }

        // Stage 1: Get favorite tour IDs
        String finalUserId = userId;
        db.collection("favorites")
                .whereEqualTo("userId", finalUserId)
                .get()
                .addOnSuccessListener(favoriteSnapshots -> {
                    if (favoriteSnapshots.isEmpty()) {
                        showEmptyState();
                        return;
                    }

                    List<String> tourIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : favoriteSnapshots) {
                        tourIds.add(doc.getString("tourId"));
                    }

                    // Stage 2: Get tour details for those IDs
                    fetchToursByIds(tourIds);
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
                        if(tour != null) {
                            tour.setId(doc.getId()); // Gán ID document vào đối tượng tour
                            favoriteToursList.add(tour);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    rvFavoriteTours.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(e -> showErrorState());
    }

    private void removeFavorite(int position) {
        String tourIdToRemove = favoriteToursList.get(position).getId();
        String userId = "DUMMY_USER_ID_123";
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) userId = currentUser.getUid();

        db.collection("favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("tourId", tourIdToRemove)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().delete(); // Xóa document trong collection 'favorites'
                    }
                    favoriteToursList.remove(position);
                    adapter.notifyItemRemoved(position);
                    if(favoriteToursList.isEmpty()) showEmptyState();
                    Toast.makeText(this, "Đã xóa khỏi Yêu thích", Toast.LENGTH_SHORT).show();
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