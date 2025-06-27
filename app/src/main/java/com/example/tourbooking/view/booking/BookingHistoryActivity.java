// File: view/booking/BookingHistoryActivity.java (Phiên bản chuẩn - Lọc trên Client)
package com.example.tourbooking.view.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tourbooking.R;
import com.example.tourbooking.adapter.BookingAdapter;
import com.example.tourbooking.model.Booking;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookingHistory;
    private BookingAdapter adapter;
    private List<Booking> allUserBookings = new ArrayList<>(); // Danh sách gốc
    private List<Booking> displayedBookings = new ArrayList<>(); // Danh sách hiển thị
    private ProgressBar progressBar;
    private TextView tvNoHistory;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ChipGroup chipGroupFilter;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchView searchView;

    private Query.Direction currentSortDirection = Query.Direction.DESCENDING;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        toolbar = findViewById(R.id.toolbar_booking_history);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupRecyclerView();
        setupFilterChips();
        setupSwipeToRefresh();
        setupItemTouchHelper();

        fetchBookingsFromFirestore();
    }

    private void initializeViews() {
        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        progressBar = findViewById(R.id.progressBar);
        tvNoHistory = findViewById(R.id.tvNoHistory);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        adapter = new BookingAdapter(displayedBookings, this);
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setAdapter(adapter);
    }

    private void fetchBookingsFromFirestore() {
        swipeRefreshLayout.setRefreshing(true);
        tvNoHistory.setVisibility(View.GONE);

        String userIdToQuery;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userIdToQuery = currentUser.getUid();
        } else {
            userIdToQuery = "DUMMY_USER_ID_123";
        }

        // Câu truy vấn đơn giản, chỉ lọc theo userId, không cần index phức hợp
        db.collection("bookings")
                .whereEqualTo("userId", userIdToQuery)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        allUserBookings.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Booking booking = document.toObject(Booking.class);
                            // Kiểm tra an toàn để tránh crash
                            if (booking.getBookingDate() != null && booking.getStatus() != null && booking.getTourName() != null) {
                                allUserBookings.add(booking);
                            }
                        }
                        applyFiltersAndSort();
                    } else {
                        Toast.makeText(BookingHistoryActivity.this, "Lỗi khi tải lịch sử.", Toast.LENGTH_SHORT).show();
                        tvNoHistory.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void applyFiltersAndSort() {
        List<Booking> tempList = new ArrayList<>(allUserBookings);

        // 1. Lọc theo trạng thái
        int checkedChipId = chipGroupFilter.getCheckedChipId();
        if (checkedChipId != R.id.chipAll) {
            String statusFilter = "";
            if (checkedChipId == R.id.chipUpcoming) statusFilter = "UPCOMING";
            else if (checkedChipId == R.id.chipCompleted) statusFilter = "COMPLETED";
            else if (checkedChipId == R.id.chipCanceled) statusFilter = "CANCELED";

            String finalStatusFilter = statusFilter;
            tempList.removeIf(booking -> !booking.getStatus().equalsIgnoreCase(finalStatusFilter));
        }

        // 2. Lọc theo từ khóa tìm kiếm
        if (searchView != null) {
            String query = searchView.getQuery().toString();
            if (!query.isEmpty()) {
                tempList.removeIf(booking -> !booking.getTourName().toLowerCase().contains(query.toLowerCase()));
            }
        }

        // 3. Sắp xếp
        Collections.sort(tempList, (b1, b2) -> {
            if (currentSortDirection == Query.Direction.DESCENDING) {
                return b2.getBookingDate().compareTo(b1.getBookingDate());
            } else {
                return b1.getBookingDate().compareTo(b2.getBookingDate());
            }
        });

        // 4. Cập nhật UI
        displayedBookings.clear();
        displayedBookings.addAll(tempList);
        adapter.notifyDataSetChanged();

        if (displayedBookings.isEmpty()) {
            tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            tvNoHistory.setVisibility(View.GONE);
        }
    }

    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> applyFiltersAndSort());
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::fetchBookingsFromFirestore);
    }

    // ... các hàm còn lại giữ nguyên
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_booking_history, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                applyFiltersAndSort();
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                applyFiltersAndSort();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            currentSortDirection = (currentSortDirection == Query.Direction.DESCENDING) ? Query.Direction.ASCENDING : Query.Direction.DESCENDING;
            Toast.makeText(this, (currentSortDirection == Query.Direction.DESCENDING) ? "Sắp xếp: Mới nhất" : "Sắp xếp: Cũ nhất", Toast.LENGTH_SHORT).show();
            applyFiltersAndSort();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupItemTouchHelper() {
        // ... giữ nguyên ...
    }
    private void cancelBooking(Booking booking) {
        // ... giữ nguyên ...
    }
    private void undoCancelBooking(Booking booking, int position) {
        // ... giữ nguyên ...
    }
}