// File: view/booking/BookingHistoryActivity.java
package com.example.tourbooking.view.booking;

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

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.BookingAdapter;
import com.example.tourbooking.model.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookingHistory;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private ProgressBar progressBar;
    private TextView tvNoHistory;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        Toolbar toolbar = findViewById(R.id.toolbar_booking_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupRecyclerView();
        loadBookingHistory();
    }

    private void initializeViews() {
        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        progressBar = findViewById(R.id.progressBar);
        tvNoHistory = findViewById(R.id.tvNoHistory);
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList, this);
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);
    }

    private void loadBookingHistory() {
        progressBar.setVisibility(View.VISIBLE);
        rvBookingHistory.setVisibility(View.GONE);
        tvNoHistory.setVisibility(View.GONE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            tvNoHistory.setVisibility(View.VISIBLE);
            return;
        }

        String userId = currentUser.getUid();

        db.collection("bookings")
                .whereEqualTo("userId", userId)
                .orderBy("bookingDate", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        bookingList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Chuyển đổi Document từ Firestore thành đối tượng Booking
                            Booking booking = document.toObject(Booking.class);
                            bookingList.add(booking);
                        }

                        if (bookingList.isEmpty()) {
                            tvNoHistory.setVisibility(View.VISIBLE);
                            rvBookingHistory.setVisibility(View.GONE);
                        } else {
                            tvNoHistory.setVisibility(View.GONE);
                            rvBookingHistory.setVisibility(View.VISIBLE);
                            adapter.setBookingList(bookingList);
                        }
                    } else {
                        Toast.makeText(BookingHistoryActivity.this, "Lỗi khi tải lịch sử.", Toast.LENGTH_SHORT).show();
                        tvNoHistory.setVisibility(View.VISIBLE);
                    }
                });
    }
}