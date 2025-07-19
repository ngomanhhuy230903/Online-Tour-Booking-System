package com.example.tourbooking.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class Tour implements Serializable {
    private String id; // ID của document
    private String tourName;
    private double price;
    private String thumbnailUrl;
    private String categoryId;
    private Integer days;
    private Float rating;

    private String description;
    private List<ItineraryItem> itinerary;
    private Float basePrice;
    private Float taxes;
    private Float fees;
    private List<String> includedServices;
    private List<String> excludedServices;
    private List<String> imageGallery;

    private String type;
    private String duration;

    // Constructor rỗng cho Firestore
    public Tour() {}

    // Constructor cơ bản
    public Tour(String id, String tourName, double price, String thumbnailUrl) {
        this.id = id;
        this.tourName = tourName;
        this.price = price;
        this.thumbnailUrl = thumbnailUrl;
    }

    // Full getters and setters
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ItineraryItem> getItinerary() {
        return itinerary;
    }

    public void setItinerary(List<ItineraryItem> itinerary) {
        this.itinerary = itinerary;
    }

    public Float getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Float basePrice) {
        this.basePrice = basePrice;
    }

    public Float getTaxes() {
        return taxes;
    }

    public void setTaxes(Float taxes) {
        this.taxes = taxes;
    }

    public Float getFees() {
        return fees;
    }

    public void setFees(Float fees) {
        this.fees = fees;
    }

    public List<String> getIncludedServices() {
        return includedServices;
    }

    public void setIncludedServices(List<String> includedServices) {
        this.includedServices = includedServices;
    }

    public List<String> getExcludedServices() {
        return excludedServices;
    }

    public void setExcludedServices(List<String> excludedServices) {
        this.excludedServices = excludedServices;
    }

    public List<String> getImageGallery() {
        return imageGallery;
    }

    public void setImageGallery(List<String> imageGallery) {
        this.imageGallery = imageGallery;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
