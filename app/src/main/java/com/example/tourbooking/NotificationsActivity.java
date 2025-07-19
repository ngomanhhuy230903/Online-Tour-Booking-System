package com.example.tourbooking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tourbooking.adapter.NotificationAdapter;
import com.example.tourbooking.model.NotificationItem;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ListView notificationsList;
    private Spinner filterByType;
    private Button markAllReadButton, settingsShortcut;
    private TextView emptyStateMessage;
    private SwipeRefreshLayout refreshControl;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "TourBookingPrefs";
    private List<NotificationItem> notifications = new ArrayList<>();
    private NotificationAdapter adapter;
    private int pageSize = 5;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notifications), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        notificationsList = findViewById(R.id.notifications_list);
        filterByType = findViewById(R.id.filter_by_type);
        markAllReadButton = findViewById(R.id.mark_all_read_button);
        settingsShortcut = findViewById(R.id.settings_shortcut);
        emptyStateMessage = findViewById(R.id.empty_state_message);
        refreshControl = findViewById(R.id.refresh_layout);

        // Gán adapter cho Spinner filter
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.notification_types,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterByType.setAdapter(filterAdapter);

        adapter = new NotificationAdapter(this, notifications);
        notificationsList.setAdapter(adapter);

        loadNotifications();

        refreshControl.setOnRefreshListener(this::refreshNotifications);
        markAllReadButton.setOnClickListener(v -> markAllRead());
        notificationsList.setOnItemClickListener((parent, view, position, id) -> {
            notifications.get(position).isUnread = false;
            adapter.notifyDataSetChanged();
        });
        settingsShortcut.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
         
         
         notificationsList.setOnItemLongClickListener((parent, view, position, id) -> {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Xóa thông báo")
            .setMessage("Bạn có chắc muốn xóa thông báo này?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                notifications.remove(position);
                adapter.notifyDataSetChanged();
            })
            .setNegativeButton("Hủy", null)
            .show();
        return true;
    });
    }

    private void loadNotifications() {
        notifications.clear();
        // Dữ liệu mẫu với icon và trạng thái đọc
        notifications.add(new NotificationItem("Notification 1", "2h ago", R.drawable.ic_calendar, true));
        notifications.add(new NotificationItem("Notification 2", "6h ago", R.drawable.ic_chat, false));
        notifications.add(new NotificationItem("Notification 3", "1d ago", R.drawable.ic_warning_2, false));
        notifications.add(new NotificationItem("Notification 4", "2d ago", R.drawable.ic_chat, false));
        adapter.notifyDataSetChanged();
    }

    private void refreshNotifications() {
        loadNotifications();
        refreshControl.setRefreshing(false);
    }

    private void markAllRead() {
        for (NotificationItem item : notifications) {
            item.isUnread = false;
        }
        adapter.notifyDataSetChanged();
    }
}