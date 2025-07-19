package com.example.tourbooking.model;

public class NotificationItem {
    public String title;
    public String time;
    public int iconRes;
    public boolean isUnread;

    public NotificationItem(String title, String time, int iconRes, boolean isUnread) {
        this.title = title;
        this.time = time;
        this.iconRes = iconRes;
        this.isUnread = isUnread;
    }
}