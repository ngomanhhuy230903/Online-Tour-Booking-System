// File: database/entity/BookingEntity.java
package com.example.tourbooking.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "bookings")
public class BookingEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String tourName;
    public long bookingDateTimestamp; // Lưu dưới dạng timestamp để dễ truy vấn
    public String status;
    public double totalPrice;
    public String userId;

    public BookingEntity() {
    }

    public BookingEntity(@NonNull String id, String tourName, long bookingDateTimestamp, String status, double totalPrice, String userId) {
        this.id = id;
        this.tourName = tourName;
        this.bookingDateTimestamp = bookingDateTimestamp;
        this.status = status;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }
}