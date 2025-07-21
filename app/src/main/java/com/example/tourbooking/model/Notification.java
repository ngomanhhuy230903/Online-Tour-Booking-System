package com.example.tourbooking.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Notification {
    public boolean isRead;
    public String message;
    public String title;
    public String type;
    public String userId;
    @ServerTimestamp
    public Date timestamp;

    public Notification() {}

    public Notification(boolean isRead, String message, String title, String type, String userId, Date timestamp) {
        this.isRead = isRead;
        this.message = message;
        this.title = title;
        this.type = type;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}