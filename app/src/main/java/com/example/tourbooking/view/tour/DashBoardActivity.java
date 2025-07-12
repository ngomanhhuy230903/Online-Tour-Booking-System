package com.example.tourbooking.view.tour;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.TourAdapter;
import com.example.tourbooking.model.Tour;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DashBoardActivity extends AppCompatActivity {

    private TextView tvRewardPoints, tvUserLevel, tvTravelCredits, tvWeatherInfo;
    private RecyclerView recyclerUpcomingTours, recyclerRecommendedTours;
    private ListView listNewNotifications, listTodayTasks;

    private TourAdapter upcomingAdapter, recommendedAdapter;
    private List<Tour> upcomingTours = new ArrayList<>();
    private List<Tour> recommendedTours = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        // Khởi tạo View
        tvRewardPoints = findViewById(R.id.tvRewardPoints);
        tvUserLevel = findViewById(R.id.tvUserLevel);
        tvTravelCredits = findViewById(R.id.tvTravelCredits);
        tvWeatherInfo = findViewById(R.id.tvWeatherInfo);

        recyclerUpcomingTours = findViewById(R.id.recyclerUpcomingTours);
        recyclerRecommendedTours = findViewById(R.id.recyclerRecommendedTours);

        listNewNotifications = findViewById(R.id.listNewNotifications);
        listTodayTasks = findViewById(R.id.listTodayTasks);

        // Cấu hình RecyclerView
        recyclerUpcomingTours.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecommendedTours.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        upcomingAdapter = new TourAdapter(this, upcomingTours);
        recommendedAdapter = new TourAdapter(this, recommendedTours);

        recyclerUpcomingTours.setAdapter(upcomingAdapter);
        recyclerRecommendedTours.setAdapter(recommendedAdapter);

        // Load dữ liệu
        loadUserData("huy123");
        loadWeather();
        loadTours();
        loadNotifications();
        loadTasks();
    }

    private void loadUserData(String userId) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        tvRewardPoints.setText("Reward Points: " + snapshot.get("rewardPoints"));
                        tvUserLevel.setText("Level: " + snapshot.get("level"));
                        tvTravelCredits.setText("Credits: $" + snapshot.get("travelCredits"));
                    }
                });
    }

    private void loadWeather() {
        tvWeatherInfo.setText("Hanoi - 32°C, Sunny");
    }

    private void loadTours() {
        FirebaseFirestore.getInstance().collection("tours")
                .get()
                .addOnSuccessListener(snapshot -> {
                    upcomingTours.clear();
                    recommendedTours.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Tour tour = doc.toObject(Tour.class);
                        upcomingTours.add(tour);
                        if (tour.getRating() != null && tour.getRating() >= 4.0) {
                            recommendedTours.add(tour);
                        }
                    }
                    upcomingAdapter.notifyDataSetChanged();
                    recommendedAdapter.notifyDataSetChanged();
                });
    }

    private void loadNotifications() {
        List<String> notifications = new ArrayList<>();
        notifications.add("Your tour is confirmed!");
        notifications.add("New tour to Nha Trang added!");
        listNewNotifications.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications));
    }

    private void loadTasks() {
        List<String> tasks = new ArrayList<>();
        tasks.add("Upload passport photo");
        tasks.add("Complete profile");
        listTodayTasks.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks));
    }
}
