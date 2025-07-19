package com.example.tourbooking.model;

import java.io.Serializable;

@SuppressWarnings("unused")
public class ItineraryItem implements Serializable {
    private String name;
    private String startTime;
    private String endTime;
    private String location;

    public ItineraryItem() {}

    public ItineraryItem(String startTime, String endTime, String location) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    // Getter & Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}

