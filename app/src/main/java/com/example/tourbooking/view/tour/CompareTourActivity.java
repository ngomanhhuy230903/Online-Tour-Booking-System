package com.example.tourbooking.view.tour;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.TourAdapter;
import com.example.tourbooking.model.Tour;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class CompareTourActivity extends AppCompatActivity {

    private RecyclerView rvCompareList;
    private TourAdapter tourAdapter;
    private List<Tour> comparedTours = new ArrayList<>();
    private TextView tvTotalCompared;
    private Spinner spinnerSortOptions;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_tour);

        rvCompareList = findViewById(R.id.rvCompareList);
        tvTotalCompared = findViewById(R.id.tvTotalCompared);
        spinnerSortOptions = findViewById(R.id.spinnerSortOptions);

        rvCompareList.setLayoutManager(new LinearLayoutManager(this));
        tourAdapter = new TourAdapter(this, comparedTours);
        rvCompareList.setAdapter(tourAdapter);

        db = FirebaseFirestore.getInstance();

        setupSortOptions();
        loadComparedTours();

        findViewById(R.id.btnExportCSV).setOnClickListener(v -> exportCSV());
        findViewById(R.id.btnShareComparison).setOnClickListener(v -> shareComparison());
    }

    private void setupSortOptions() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortOptions.setAdapter(adapter);

        spinnerSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortTours(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadComparedTours() {
        db.collection("compareTours")
                .document("demoCompare") // Hoặc userId nếu có Auth
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        List<String> tourIds = (List<String>) snapshot.get("tours");
                        if (tourIds != null && !tourIds.isEmpty()) {
                            comparedTours.clear();
                            for (String tourId : tourIds) {
                                db.collection("tours").document(tourId)
                                        .get()
                                        .addOnSuccessListener(tourSnap -> {
                                            if (tourSnap.exists()) {
                                                Tour tour = tourSnap.toObject(Tour.class);
                                                if (tour != null) {
                                                    comparedTours.add(tour);
                                                    tourAdapter.notifyDataSetChanged();
                                                    tvTotalCompared.setText("Comparing: " + comparedTours.size() + " tours");
                                                }
                                            }
                                        });
                            }
                        } else {
                            tvTotalCompared.setText("No tours selected for comparison.");
                        }
                    } else {
                        tvTotalCompared.setText("Comparison list not found.");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load comparison list.", Toast.LENGTH_SHORT).show());
    }

    private void sortTours(int option) {
        switch (option) {
            case 0:
                Collections.sort(comparedTours, Comparator.comparingDouble(Tour::getPrice));
                break;
            case 1:
                Collections.sort(comparedTours, (a, b) -> Double.compare(b.getRating(), a.getRating()));
                break;
            case 2:
                Collections.sort(comparedTours, Comparator.comparingInt(Tour::getDays));
                break;
        }
        tourAdapter.notifyDataSetChanged();
    }

    private void exportCSV() {
        StringBuilder sb = new StringBuilder("Name,Price,Rating,Days\n");
        for (Tour t : comparedTours) {
            sb.append(t.getTourName()).append(",")
                    .append(t.getPrice()).append(",")
                    .append(t.getRating()).append(",")
                    .append(t.getDays()).append("\n");
        }

        // 🚧 Ghi vào file hoặc export tùy platform (tạm thời toast)
        Toast.makeText(this, "CSV exported (simulated)", Toast.LENGTH_SHORT).show();
    }

    private void shareComparison() {
        StringBuilder sb = new StringBuilder("Tour Comparison:\n");
        for (Tour t : comparedTours) {
            sb.append(t.getTourName())
                    .append(" - $").append(t.getPrice())
                    .append(" - ").append(t.getRating()).append("★")
                    .append(" - ").append(t.getDays()).append(" days\n");
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Tour Comparison");
        intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        startActivity(Intent.createChooser(intent, "Share using"));
    }
}
