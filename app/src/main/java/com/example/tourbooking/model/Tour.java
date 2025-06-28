package com.example.tourbooking.model;

public class Tour {
    public String id; // ID của document
    public String tourName;
    public double price;
    public String thumbnailUrl;

    public Tour() {} // Constructor rỗng cho Firestore

    public Tour(String id, String tourName, double price, String thumbnailUrl) {
        this.id = id;
        this.tourName = tourName;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}