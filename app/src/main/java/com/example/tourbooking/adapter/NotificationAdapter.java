package com.example.tourbooking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tourbooking.R;
import com.example.tourbooking.model.NotificationItem;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    private final Context context;
    private final List<NotificationItem> notifications;

    public NotificationAdapter(Context context, List<NotificationItem> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public int getCount() {
        return notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationItem item = notifications.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        }
        ImageView imgType = convertView.findViewById(R.id.imgType);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvTime = convertView.findViewById(R.id.tvTime);
        View unreadDot = convertView.findViewById(R.id.unreadDot);

        imgType.setImageResource(item.iconRes);
        tvTitle.setText(item.title);
        tvTime.setText(item.time);
        unreadDot.setVisibility(item.isUnread ? View.VISIBLE : View.GONE);

        return convertView;
    }
}