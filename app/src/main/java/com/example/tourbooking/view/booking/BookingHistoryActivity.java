// File: view/booking/BookingHistoryActivity.java (Hoàn thiện với Pagination)
package com.example.tourbooking.view.booking;

import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot; // Import mới
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
    private ChipGroup chipGroupFilter;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;

    // === CÁC BIẾN MỚI ĐỂ QUẢN LÝ PAGINATION ===
    private String currentStatusFilter = "ALL";
    private String currentSearchQuery = "";
    private Query.Direction currentSortDirection = Query.Direction.DESCENDING;
    private boolean isLoading = false; // Cờ để tránh tải nhiều trang cùng lúc
    private boolean isLastPage = false; // Cờ để biết đã đến trang cuối cùng chưa
    private DocumentSnapshot lastVisibleDocument = null; // Lưu lại document cuối cùng của trang trước
    private static final long PAGE_SIZE = 10; // Số lượng item mỗi trang

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        // ... (phần khởi tạo ban đầu giữ nguyên)
        toolbar = findViewById(R.id.toolbar_booking_history);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupRecyclerView();
        setupFilterChips();
        setupSwipeToRefresh();
        setupItemTouchHelper();

        loadInitialBookings(); // Tải trang đầu tiên
    }

    private void initializeViews() {
        // ... (giữ nguyên)
        rvBookingHistory = findViewById(R.id.rvBookingHistory);
        progressBar = findViewById(R.id.progressBar);
        tvNoHistory = findViewById(R.id.tvNoHistory);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        adapter = new BookingAdapter(bookingList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvBookingHistory.setLayoutManager(layoutManager);
        rvBookingHistory.setAdapter(adapter);

        // === THÊM SỰ KIỆN LẮNG NGHE CUỘN ĐỂ TẢI THÊM ===
        rvBookingHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Điều kiện để tải trang tiếp theo
                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        fetchBookings(false); // Tải trang tiếp theo
                    }
                }
            }
        });
    }

    // === CÁC HÀM XỬ LÝ SỰ KIỆN SẼ GỌI loadInitialBookings() ĐỂ RESET VÀ TẢI LẠI TỪ ĐẦU ===
    private void setupFilterChips() {
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) currentStatusFilter = "ALL";
            else if (checkedId == R.id.chipUpcoming) currentStatusFilter = "UPCOMING";
            else if (checkedId == R.id.chipCompleted) currentStatusFilter = "COMPLETED";
            else if (checkedId == R.id.chipCanceled) currentStatusFilter = "CANCELED";
            loadInitialBookings(); // Reset và tải lại từ đầu
        });
    }

    private void setupSwipeToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadInitialBookings); // Reset và tải lại từ đầu
    }

    // Hàm để reset và tải lại từ trang đầu tiên
    private void loadInitialBookings() {
        isLastPage = false;
        lastVisibleDocument = null;
        bookingList.clear();
        adapter.notifyDataSetChanged();
        fetchBookings(true); // isInitialLoad = true
    }

    // === HÀM fetchBookings() được nâng cấp toàn diện cho PAGINATION ===
    private void fetchBookings(boolean isInitialLoad) {
        if (isLoading) return; // Nếu đang tải rồi thì không làm gì cả
        isLoading = true;

        if (isInitialLoad && !swipeRefreshLayout.isRefreshing()) {
            progressBar.setVisibility(View.VISIBLE);
        }
        tvNoHistory.setVisibility(View.GONE);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) { /* ... (giữ nguyên logic xử lý user null) ... */ return; }

        // Xây dựng câu truy vấn
        Query query = db.collection("bookings")
                .whereEqualTo("userId", currentUser.getUid())
                .limit(PAGE_SIZE); // Luôn giới hạn số lượng item mỗi lần tải

        // Áp dụng các bộ lọc và sắp xếp
        if (!currentStatusFilter.equals("ALL")) {
            query = query.whereEqualTo("status", currentStatusFilter);
        }
        if (!currentSearchQuery.isEmpty()) {
            query = query.orderBy("tourName").startAt(currentSearchQuery).endAt(currentSearchQuery + '\uf8ff');
        } else {
            query = query.orderBy("bookingDate", currentSortDirection);
        }

        // Nếu đây không phải lần tải đầu tiên, bắt đầu truy vấn từ item cuối cùng của lần trước
        if (!isInitialLoad && lastVisibleDocument != null) {
            query = query.startAfter(lastVisibleDocument);
        }

        query.get().addOnCompleteListener(task -> {
            isLoading = false;
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            if (task.isSuccessful() && task.getResult() != null) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();

                // Chuyển đổi documents thành danh sách Bookings
                List<Booking> newBookings = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    newBookings.add(document.toObject(Booking.class));
                }

                // Nối danh sách mới vào danh sách cũ
                bookingList.addAll(newBookings);
                adapter.notifyDataSetChanged();

                // Kiểm tra xem đây có phải trang cuối cùng không
                if (documents.size() < PAGE_SIZE) {
                    isLastPage = true;
                }

                // Lưu lại document cuối cùng để làm điểm bắt đầu cho lần tải tiếp theo
                if (!documents.isEmpty()) {
                    lastVisibleDocument = documents.get(documents.size() - 1);
                }

                // Xử lý thông báo "Không có lịch sử"
                if (bookingList.isEmpty()) {
                    tvNoHistory.setVisibility(View.VISIBLE);
                }

            } else {
                Toast.makeText(BookingHistoryActivity.this, "Lỗi khi tải lịch sử.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ... (Các hàm còn lại như setupItemTouchHelper, cancelBooking, onCreateOptionsMenu, onOptionsItemSelected... giữ nguyên)
    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Booking bookingToCancel = bookingList.get(position);
                if ("UPCOMING".equalsIgnoreCase(bookingToCancel.getStatus())) {
                    cancelBooking(bookingToCancel, position);
                } else {
                    adapter.notifyItemChanged(position);
                    Snackbar.make(rvBookingHistory, "Chỉ có thể hủy các tour Sắp tới.", Snackbar.LENGTH_LONG).show();
                }
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rvBookingHistory);
    }
    private void cancelBooking(Booking booking, int position) {
        db.collection("bookings").document(booking.getId()).update("status", "CANCELED")
                .addOnSuccessListener(aVoid -> {
                    booking.setStatus("CANCELED");
                    adapter.notifyItemChanged(position);
                    Snackbar.make(rvBookingHistory, "Đã hủy đơn hàng #" + booking.getId().substring(booking.getId().length() - 7), Snackbar.LENGTH_LONG)
                            .setAction("Hoàn tác", v -> undoCancelBooking(booking, position)).show();
                })
                .addOnFailureListener(e -> {
                    adapter.notifyItemChanged(position);
                    Toast.makeText(this, "Lỗi: Không thể hủy đơn hàng.", Toast.LENGTH_SHORT).show();
                });
    }
    private void undoCancelBooking(Booking booking, int position) {
        db.collection("bookings").document(booking.getId()).update("status", "UPCOMING")
                .addOnSuccessListener(aVoid -> {
                    booking.setStatus("UPCOMING");
                    adapter.notifyItemChanged(position);
                    Toast.makeText(this, "Đã hoàn tác.", Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_booking_history, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                loadInitialBookings(); // Reset và tìm kiếm từ đầu
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty() && !currentSearchQuery.isEmpty()) {
                    currentSearchQuery = "";
                    loadInitialBookings();
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_sort) {
            currentSortDirection = (currentSortDirection == Query.Direction.DESCENDING) ? Query.Direction.ASCENDING : Query.Direction.DESCENDING;
            Toast.makeText(this, (currentSortDirection == Query.Direction.DESCENDING) ? "Sắp xếp: Mới nhất đến cũ nhất" : "Sắp xếp: Cũ nhất đến mới nhất", Toast.LENGTH_SHORT).show();
            loadInitialBookings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}