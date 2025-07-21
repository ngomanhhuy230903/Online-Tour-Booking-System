package com.example.tourbooking.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourbooking.R;
import com.example.tourbooking.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private Context context;

    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    public void setNotificationList(List<Notification> notificationList) {
        this.notificationList = notificationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.bind(notificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNotificationIcon;
        TextView tvNotificationTitle, tvNotificationMessage, tvNotificationTimestamp;
        View viewReadIndicator;
        LinearLayout notificationItemLayout;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNotificationIcon = itemView.findViewById(R.id.ivNotificationIcon);
            tvNotificationTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvNotificationMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvNotificationTimestamp = itemView.findViewById(R.id.tvNotificationTimestamp);
            viewReadIndicator = itemView.findViewById(R.id.viewReadIndicator);
            notificationItemLayout = itemView.findViewById(R.id.notification_item_layout);
        }

        void bind(Notification notification) {
            tvNotificationTitle.setText(notification.title);
            tvNotificationMessage.setText(notification.message);

            if (notification.timestamp != null) {
                long now = System.currentTimeMillis();
                CharSequence relativeTime = DateUtils.getRelativeTimeSpanString(
                        notification.timestamp.getTime(), now, DateUtils.MINUTE_IN_MILLIS);
                tvNotificationTimestamp.setText(relativeTime);
            }

            if (notification.isRead) {
                viewReadIndicator.setVisibility(View.GONE);
                notificationItemLayout.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                tvNotificationTitle.setTypeface(null, Typeface.NORMAL);
            } else {
                viewReadIndicator.setVisibility(View.VISIBLE);
                notificationItemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.unread_background));
                tvNotificationTitle.setTypeface(null, Typeface.BOLD);
            }

            switch (notification.type.toUpperCase()) {
                case "PROMOTION":
                    ivNotificationIcon.setImageResource(R.drawable.ic_promotion);
                    break;
                case "UPDATE":
                    ivNotificationIcon.setImageResource(R.drawable.ic_update);
                    break;
                default:
                    ivNotificationIcon.setImageResource(R.drawable.ic_notifications);
                    break;
            }
        }
    }
}