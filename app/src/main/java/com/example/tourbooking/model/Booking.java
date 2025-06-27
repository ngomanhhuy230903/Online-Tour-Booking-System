// File: model/Booking.java
package com.example.tourbooking.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Booking {
    public String id;
    public String tourName;
    public String status;
    public double totalPrice;
    public String userId;
    @ServerTimestamp
    public Date bookingDate;

    public Booking() {
    }

    public Booking(String id, String tourName, String status, double totalPrice, String userId, Date bookingDate) {
        this.id = id;
        this.tourName = tourName;
        this.status = status;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.bookingDate = bookingDate;
    }

    // Các hàm getter
    public String getId() { return id; }
    public String getTourName() { return tourName; }
    public String getStatus() { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getUserId() { return userId; }
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

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}