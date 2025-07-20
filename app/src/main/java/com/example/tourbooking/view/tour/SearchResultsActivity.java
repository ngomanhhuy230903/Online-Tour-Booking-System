package com.example.tourbooking.view.tour;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.TourAdapter;
import com.example.tourbooking.model.Category;
import com.example.tourbooking.model.ItineraryItem;
import com.example.tourbooking.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchResultsActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerSort;
    private TextView tvFilterInfo;
    List<Tour> tourList = new ArrayList<>();
    TourAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        tvFilterInfo = findViewById(R.id.tvFilterInfo);

        String categoryId = getIntent().getStringExtra("category"); // Beach, Mountain...
        if (categoryId != null && !categoryId.isEmpty()) {
            db.collection("categories").document(categoryId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Category category = documentSnapshot.toObject(Category.class);
                            if (category != null) {
                                tvFilterInfo.setText(category.getName());
                            } else {
                                tvFilterInfo.setText("Category not found.");
                            }
                        } else {
                            tvFilterInfo.setText("Không có dữ liệu cho categoryId: " + categoryId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvFilterInfo.setText("Lỗi khi tải danh mục.");
                    });
            loadToursByCategory(categoryId);
        }

        String keyword = getIntent().getStringExtra("keyword");
        String location = getIntent().getStringExtra("location");
        String type = getIntent().getStringExtra("type");
        String duration = getIntent().getStringExtra("duration");
        float minPrice = getIntent().getFloatExtra("minPrice", 0);
        float maxPrice = getIntent().getFloatExtra("maxPrice", 10000);
        long departureDate = getIntent().getLongExtra("departureDate", 0);
        long returnDate = getIntent().getLongExtra("returnDate", 0);
        if (keyword != null || location != null || type != null ||
                duration != null || minPrice != 0 || maxPrice != 10000 || departureDate != 0 || returnDate != 0) {
            loadToursBySearch(keyword, location, type, duration, minPrice, maxPrice, departureDate, returnDate);
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
                        sortToursByViewCount();
                        break;
                    default:
                        break;
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

    private void sortToursByViewCount() {
        Collections.sort(tourList, (t1, t2) -> Long.compare(t2.getViewCount(), t1.getViewCount()));
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
                        tour.setId(doc.getId());
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

    private void loadToursBySearch(String keyword, String location, String type, String duration, float minPrice, float maxPrice, long departureDate, long returnDate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tours")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    tourList = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Tour tour = doc.toObject(Tour.class);
                        tour.setId(doc.getId());

                        boolean matches = true;

                        // 1. keyword trong tên tour
                        if (keyword != null && !keyword.isEmpty()) {
                            if (tour.getTourName() == null || !tour.getTourName().toLowerCase().contains(keyword.toLowerCase())) {
                                matches = false;
                            }
                        }

                        // 2. location chỉ cần xuất hiện trong 1 ItineraryItem
                        if (location != null && !location.isEmpty() && tour.getItinerary() != null) {
                            boolean locationMatch = false;
                            for (ItineraryItem item : tour.getItinerary()) {
                                if (item.getLocation() != null && item.getLocation().toLowerCase().contains(location.toLowerCase())) {
                                    locationMatch = true;
                                    break;
                                }
                            }
                            if (!locationMatch) matches = false;
                        }

                        // 3. date (so sánh với tour.startTime và tour.endTime)
                        try {
                            if (departureDate > 0 && tour.getStartTime() != null) {
                                Date tourStart = sdf.parse(tour.getStartTime());
                                if (tourStart == null || tourStart.getTime() < departureDate) {
                                    matches = false;
                                }
                            }

                            if (returnDate > 0 && tour.getEndTime() != null) {
                                Date tourEnd = sdf.parse(tour.getEndTime());
                                if (tourEnd == null || tourEnd.getTime() > returnDate) {
                                    matches = false;
                                }
                            }
                        } catch (ParseException e) {
                            matches = false;
                        }

                        // 4. price
                        if (tour.getPrice() < minPrice) matches = false;
                        if (tour.getPrice() > maxPrice) matches = false;

                        // 5. type
                        if (type != null && !type.isEmpty()) {
                            if (!type.equalsIgnoreCase(tour.getType())) matches = false;
                        }

                        // 6. duration
                        if (duration != null && !duration.isEmpty()) {
                            if (!duration.equalsIgnoreCase(tour.getDuration())) matches = false;
                        }

                        if (matches) {
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