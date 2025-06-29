package com.example.tourbooking.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.List;
import java.util.Date;

public class Review {
    public String userId;
    public String userName;
    public String tourId;
    public float rating;
    public String comment;
    public List<String> photoUrls; // Danh sách các URL ảnh
    @ServerTimestamp
    public Date timestamp;

    public Review() {} // Constructor rỗng

    public Review(String userId, String userName, String tourId, float rating, String comment, List<String> photoUrls, Date timestamp) {
        this.userId = userId;
        this.userName = userName;
        this.tourId = tourId;
        this.rating = rating;
        this.comment = comment;
        this.photoUrls = photoUrls;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getPhotoUrls() {
        return photoUrls;
    }

    public void setPhotoUrls(List<String> photoUrls) {
        this.photoUrls = photoUrls;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}