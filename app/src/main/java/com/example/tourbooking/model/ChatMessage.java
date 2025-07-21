package com.example.tourbooking.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class ChatMessage {
    private String senderId;
    private String text;
    @ServerTimestamp
    private Date timestamp;

    public ChatMessage() {}

    public String getSenderId() { return senderId; }
    public String getText() { return text; }
    public Date getTimestamp() { return timestamp; }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}