// File: model/Booking.java
package com.example.tourbooking.model;

import java.util.Date;

public class Booking {
    private String id;
    private String tourName;
    private Date bookingDate;
    private String status;
    private double totalPrice;
    private String userId;

    // Constructors, Getters, and Setters
    public Booking() {}

    public Booking(String id, String tourName, Date bookingDate, String status, double totalPrice, String userId) {
        this.id = id;
        this.tourName = tourName;
        this.bookingDate = bookingDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}