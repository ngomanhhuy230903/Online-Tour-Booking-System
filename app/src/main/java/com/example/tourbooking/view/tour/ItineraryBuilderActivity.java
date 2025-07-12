package com.example.tourbooking.view.tour;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tourbooking.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.*;

public class ItineraryBuilderActivity extends AppCompatActivity {

    private LinearLayout itemListContainer;
    private TextView estimatedCostText, totalDaysText;
    private CheckBox editModeToggle;
    private double totalCost = 0.0;
    private int activityCount = 0;

    private FirebaseFirestore db;
    private DatePicker itineraryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_builder);

        // View Binding
        itemListContainer = findViewById(R.id.itemListContainer);
        estimatedCostText = findViewById(R.id.estimatedCost);
        totalDaysText = findViewById(R.id.totalDays);
        editModeToggle = findViewById(R.id.editModeToggle);
        itineraryDate = findViewById(R.id.itineraryDate);
        db = FirebaseFirestore.getInstance();

        Button addItemButton = findViewById(R.id.addItemButton);
        addItemButton.setOnClickListener(v -> addItineraryItem(null, null));

        Button saveButton = findViewById(R.id.saveItinerary);
        saveButton.setOnClickListener(v -> saveItineraryToFirestore());

        findViewById(R.id.downloadPDF).setOnClickListener(v -> {
            Toast.makeText(this, "Download PDF feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.shareItinerary).setOnClickListener(v -> {
            Toast.makeText(this, "Share feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        editModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> updateEditableState(isChecked));
    }

    private void addItineraryItem(String location, String cost) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_itinerary_input, itemListContainer, false);

        EditText locationInput = itemView.findViewById(R.id.locationInput);
        EditText costInput = itemView.findViewById(R.id.costInput);
        ImageButton deleteButton = itemView.findViewById(R.id.deleteButton);

        if (location != null) locationInput.setText(location);
        if (cost != null) costInput.setText(cost);

        deleteButton.setOnClickListener(v -> {
            String costStr = costInput.getText().toString();
            if (!costStr.isEmpty()) {
                totalCost -= Double.parseDouble(costStr);
            }
            itemListContainer.removeView(itemView);
            activityCount--;
            updateSummary();
        });

        itemListContainer.addView(itemView);
        activityCount++;
        updateSummary();
    }

    private void updateEditableState(boolean editable) {
        for (int i = 0; i < itemListContainer.getChildCount(); i++) {
            View item = itemListContainer.getChildAt(i);
            item.findViewById(R.id.locationInput).setEnabled(editable);
            item.findViewById(R.id.costInput).setEnabled(editable);
            item.findViewById(R.id.deleteButton).setVisibility(editable ? View.VISIBLE : View.GONE);
        }
    }

    private void updateSummary() {
        totalCost = 0.0;
        for (int i = 0; i < itemListContainer.getChildCount(); i++) {
            View item = itemListContainer.getChildAt(i);
            EditText costInput = item.findViewById(R.id.costInput);
            String costStr = costInput.getText().toString();
            if (!costStr.isEmpty()) {
                totalCost += Double.parseDouble(costStr);
            }
        }
        estimatedCostText.setText(String.valueOf(totalCost));
        totalDaysText.setText(String.valueOf(Math.max(1, activityCount)));
    }

    private void saveItineraryToFirestore() {
        List<Map<String, Object>> activities = new ArrayList<>();

        for (int i = 0; i < itemListContainer.getChildCount(); i++) {
            View item = itemListContainer.getChildAt(i);
            EditText locationInput = item.findViewById(R.id.locationInput);
            EditText costInput = item.findViewById(R.id.costInput);

            String location = locationInput.getText().toString().trim();
            String costStr = costInput.getText().toString().trim();

            if (!location.isEmpty() && !costStr.isEmpty()) {
                try {
                    double cost = Double.parseDouble(costStr);
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("location", location);
                    activity.put("cost", cost);
                    activities.add(activity);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid cost at item " + (i + 1), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(itineraryDate.getYear(), itineraryDate.getMonth(), itineraryDate.getDayOfMonth());
        Date date = calendar.getTime();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);

        Map<String, Object> itinerary = new HashMap<>();
        itinerary.put("userId", "huy123"); // TODO: thay bằng user ID thực tế nếu có
        itinerary.put("date", formattedDate);
        itinerary.put("activities", activities);
        itinerary.put("totalCost", totalCost);
        itinerary.put("days", Math.max(1, activityCount));

        db.collection("itineraries")
                .add(itinerary)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
