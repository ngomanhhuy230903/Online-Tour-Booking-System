package com.example.tourbooking.view.tour;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.TourAdapter;
import com.example.tourbooking.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class CompareTourActivity extends AppCompatActivity {

    private RecyclerView rvCompareList;
    private TourAdapter tourAdapter;
    private List<Tour> comparedTours = new ArrayList<>();
    private TextView tvTotalCompared;
    private FirebaseFirestore db;
    private Button btnCompareSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_tour);

        rvCompareList = findViewById(R.id.rvCompareList);
        tvTotalCompared = findViewById(R.id.tvTotalCompared);
        btnCompareSelected = findViewById(R.id.btnCompareSelected);

        rvCompareList.setLayoutManager(new LinearLayoutManager(this));
        tourAdapter = new TourAdapter(this, comparedTours);
        rvCompareList.setAdapter(tourAdapter);

        db = FirebaseFirestore.getInstance();

        loadComparedTours();

        findViewById(R.id.btnExportCSV).setOnClickListener(v -> exportCSV());
        findViewById(R.id.btnShareComparison).setOnClickListener(v -> shareComparison());
        btnCompareSelected.setOnClickListener(v -> compareSelectedTours());
    }

    private void loadComparedTours() {
        db.collection("tours")
                .get()
                .addOnSuccessListener(snapshot -> {
                    comparedTours.clear();
                    snapshot.forEach(tourSnap -> {
                        Tour tour = tourSnap.toObject(Tour.class);
                        if (tour != null) {
                            comparedTours.add(tour);
                        }
                    });
                    tourAdapter.notifyDataSetChanged();
                    tvTotalCompared.setText("Total tours: " + comparedTours.size());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load tours", Toast.LENGTH_SHORT).show());
    }

    private void compareSelectedTours() {
        Set<Tour> selectedTours = tourAdapter.getSelectedTours();
        if (selectedTours.size() != 2) {
            Toast.makeText(this, "Please select exactly 2 tours to compare.", Toast.LENGTH_SHORT).show();
            return;
        }

        Iterator<Tour> iterator = selectedTours.iterator();
        Tour tour1 = iterator.next();
        Tour tour2 = iterator.next();

        Intent intent = new Intent(this, CompareResultActivity.class);
        intent.putExtra("tour1", tour1);
        intent.putExtra("tour2", tour2);
        startActivity(intent);
    }


    private void exportCSV() {
        StringBuilder sb = new StringBuilder("Name,Price,Rating,Days\n");
        for (Tour t : comparedTours) {
            sb.append(t.getTourName()).append(",")
                    .append(t.getPrice()).append(",")
                    .append(t.getRating()).append(",")
                    .append(t.getDays()).append("\n");
        }

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
