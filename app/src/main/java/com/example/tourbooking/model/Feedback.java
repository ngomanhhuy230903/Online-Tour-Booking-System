package com.example.tourbooking.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Feedback {
    public String userId;
    public float rating;
    public String feedbackText;
    public String screenshotUrl;
    @ServerTimestamp
    public Date timestamp;

    public Feedback() {
    }

}