// File: view/booking/BookingHistoryActivity.java (Hoàn thiện tất cả chức năng M17)
package com.example.tourbooking.view.booking;

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
import java.util.stream.Collectors;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView rvBookingHistory;
    private BookingAdapter adapter;
    private List<Booking> allUserBookings = new ArrayList<>(); // Danh sách gốc chứa TẤT CẢ booking
    private List<Booking> displayedBookings = new ArrayList<>(); // Danh sách đã lọc để hiển thị
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
        setupItemTouchHelper(); // <-- Gọi hàm đã được hoàn thiện

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
        rvBookingHistory.setVisibility(View.GONE);

        String userIdToQuery;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userIdToQuery = currentUser.getUid();
        } else {
            userIdToQuery = "DUMMY_USER_ID_123";
        }

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
        if (checkedChipId != -1 && checkedChipId != R.id.chipAll) { // -1 là trường hợp không có chip nào được chọn
            String statusFilter = "";
            if (checkedChipId == R.id.chipUpcoming) statusFilter = "UPCOMING";
            else if (checkedChipId == R.id.chipCompleted) statusFilter = "COMPLETED";
            else if (checkedChipId == R.id.chipCanceled) statusFilter = "CANCELED";

            if (!statusFilter.isEmpty()) {
                String finalStatusFilter = statusFilter;
                tempList.removeIf(booking -> !booking.getStatus().equalsIgnoreCase(finalStatusFilter));
            }
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
            rvBookingHistory.setVisibility(View.GONE);
        } else {
            tvNoHistory.setVisibility(View.GONE);
            rvBookingHistory.setVisibility(View.VISIBLE);
        }
    }

    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> applyFiltersAndSort());
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::fetchBookingsFromFirestore);
    }

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

    // === PHẦN CODE ĐÃ ĐƯỢC HOÀN THIỆN ===
    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Không cần kéo thả
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return; // Vị trí không hợp lệ, không làm gì cả
                }

                // Lấy item từ danh sách đang hiển thị trên màn hình
                final Booking bookingToCancel = displayedBookings.get(position);

                // Chỉ cho phép hủy các tour có trạng thái "UPCOMING"
                if ("UPCOMING".equalsIgnoreCase(bookingToCancel.getStatus())) {
                    performCancelAction(bookingToCancel);
                } else {
                    // Nếu trạng thái khác, không cho hủy và cho item trượt về vị trí cũ
                    adapter.notifyItemChanged(position);
                    Snackbar.make(rvBookingHistory, "Chỉ có thể hủy các tour Sắp tới.", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rvBookingHistory);
    }

    private void performCancelAction(final Booking booking) {
        // Cập nhật trạng thái trên Firestore
        db.collection("bookings").document(booking.getId())
                .update("status", "CANCELED")
                .addOnSuccessListener(aVoid -> {
                    // Hiển thị thông báo với lựa chọn hoàn tác
                    Snackbar.make(rvBookingHistory, "Đã hủy đơn hàng.", Snackbar.LENGTH_LONG)
                            .setAction("Hoàn tác", v -> undoCancelAction(booking))
                            .show();
                    // Tải lại toàn bộ dữ liệu từ Firestore để đảm bảo giao diện được cập nhật chính xác
                    fetchBookingsFromFirestore();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi: Không thể hủy đơn hàng.", Toast.LENGTH_SHORT).show();
                    // Tải lại để item quay về trạng thái cũ nếu hủy thất bại
                    fetchBookingsFromFirestore();
                });
    }

    private void undoCancelAction(final Booking booking) {
        // Cập nhật lại trạng thái trên Firebase về "UPCOMING"
        db.collection("bookings").document(booking.getId())
                .update("status", "UPCOMING")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã hoàn tác.", Toast.LENGTH_SHORT).show();
                    // Tải lại toàn bộ dữ liệu để đảm bảo tính nhất quán
                    fetchBookingsFromFirestore();
                });
    }
}