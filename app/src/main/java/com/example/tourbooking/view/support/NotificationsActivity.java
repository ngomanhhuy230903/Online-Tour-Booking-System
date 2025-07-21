package com.example.tourbooking.view.support;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.tourbooking.R;
import com.example.tourbooking.adapter.NotificationAdapter;
import com.example.tourbooking.model.Notification;
import com.example.tourbooking.utils.SessionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private ProgressBar progressBar;
    private TextView tvNoNotifications;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Toolbar toolbar = findViewById(R.id.toolbar_notifications);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        sessionManager = new SessionManager(this);

        initializeViews();
        setupRecyclerView();
        loadNotifications();
    }

    private void initializeViews() {
        rvNotifications = findViewById(R.id.rvNotifications);
        progressBar = findViewById(R.id.progressBarNotifications);
        tvNoNotifications = findViewById(R.id.tvNoNotifications);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutNotifications);
        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        if (!swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        tvNoNotifications.setVisibility(View.GONE);
        rvNotifications.setVisibility(View.GONE);

        String userId = sessionManager.getUserId();
        if (userId == null) {
            // Dùng ID test nếu người dùng chưa đăng nhập
            userId = "DUMMY_USER_ID_123";
            Toast.makeText(this, "Chưa đăng nhập, hiển thị thông báo mẫu", Toast.LENGTH_SHORT).show();
        }

        db.collection("notifications")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    if (task.isSuccessful() && task.getResult() != null) {
                        notificationList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            notificationList.add(document.toObject(Notification.class));
                        }
                        adapter.setNotificationList(notificationList);

                        if (notificationList.isEmpty()) {
                            tvNoNotifications.setVisibility(View.VISIBLE);
                        } else {
                            rvNotifications.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvNoNotifications.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "Lỗi khi tải thông báo.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}