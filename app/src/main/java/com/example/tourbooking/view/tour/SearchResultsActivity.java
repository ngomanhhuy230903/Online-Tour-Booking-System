package com.example.tourbooking.view.tour;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.TourAdapter;
import com.example.tourbooking.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerSort;
    List<Tour> tourList = new ArrayList<>();
    TourAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        String categoryName = getIntent().getStringExtra("category"); // Beach, Mountain...
        if (categoryName != null) {
            loadToursByCategory(categoryName);
        }

        String keyword = getIntent().getStringExtra("keyword");
        String location = getIntent().getStringExtra("location");
        String type = getIntent().getStringExtra("type");
        String duration = getIntent().getStringExtra("duration");
        float minPrice = getIntent().getFloatExtra("minPrice", 0);
        float maxPrice = getIntent().getFloatExtra("maxPrice", 10000);
        if (keyword != null || location != null || type != null ||
                duration != null || minPrice != 0 || maxPrice != 10000) {
            loadToursBySearch(keyword, location, type, duration, minPrice, maxPrice);
        }

        spinnerSort = findViewById(R.id.spinnerSort);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();

                switch (selectedOption) {
                    case "Price: Low to High":
                        sortToursByPrice(true); // ascending
                        break;
                    case "Price: High to Low":
                        sortToursByPrice(false); // descending
                        break;
                    case "Top Rated":
                        sortToursByRating(); // descending
                        break;
                    case "Popular":
                        //
                        break;
                    // bạn có thể xử lý thêm các trường hợp khác nếu muốn
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void sortToursByPrice(boolean ascending) {
        if (ascending) {
            Collections.sort(tourList, Comparator.comparingDouble(Tour::getPrice));
        } else {
            Collections.sort(tourList, (t1, t2) -> Double.compare(t2.getPrice(), t1.getPrice()));
        }

        adapter.notifyDataSetChanged(); // update giao diện
    }

    private void sortToursByRating() {
        Collections.sort(tourList, (t1, t2) -> Float.compare(t2.getRating(), t1.getRating()));
        adapter.notifyDataSetChanged();
    }

    private void loadToursByCategory(String categoryId) {
        db.collection("tours")
            .whereEqualTo("categoryId", categoryId)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    tourList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Tour tour = doc.toObject(Tour.class);
                        tourList.add(tour);
                    }

                    // Gắn adapter
                    adapter = new TourAdapter(this, tourList);
                    RecyclerView recyclerView = findViewById(R.id.rvTours);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "Load tour failed", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loadToursBySearch(String keyword, String location, String type, String duration, float minPrice, float maxPrice) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tours")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tourList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Tour tour = doc.toObject(Tour.class);

                        // Lọc dữ liệu
                        boolean match =
                                (keyword.isEmpty() || (tour.getTourName() != null && tour.getTourName().toLowerCase().contains(keyword.toLowerCase())))
                                        //&& (location.isEmpty() || tour.getLocation().equalsIgnoreCase(location))
                                        && (type.equals("All") || (tour.getType() != null && tour.getType().equalsIgnoreCase(type)))
                                        && (duration.equals("All") || (tour.getDuration() != null && tour.getDuration().equalsIgnoreCase(duration)))
                                        && (tour.getPrice() >= minPrice && tour.getPrice() <= maxPrice);

                        if (match) {
                            tourList.add(tour);
                        }
                    }

                    // Gắn adapter
                    adapter = new TourAdapter(this, tourList);
                    RecyclerView recyclerView = findViewById(R.id.rvTours);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load tours", Toast.LENGTH_SHORT).show();
                });
    }

}