package com.example.tourbooking.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Booking {
    public String id;
    public String tourName;
    public String status;
    public double totalPrice;
    public String userId;
    public String tourId;
    @ServerTimestamp
    public Date bookingDate;

    public Booking() {

    }

    public Booking(String id, String tourName, String status, double totalPrice, String userId, String tourId, Date bookingDate) {
        this.id = id;
        this.tourName = tourName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.tourId = tourId;
        this.bookingDate = bookingDate;
    }

    // Các hàm getter
    public String getId() { return id; }
    public String getTourName() { return tourName; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getUserId() { return userId; }
    public String getTourId() { return tourId; } // <-- Getter mới
    public Date getBookingDate() { return bookingDate; }

    public void setId(String id) {
        this.id = id;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}