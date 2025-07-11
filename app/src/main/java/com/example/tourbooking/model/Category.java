package com.example.tourbooking.model;

public class Category {
    private String name;
    private String iconUrl;
    private String description;

    public Category() {}

    public Category(String name, String iconUrl, String description) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.description = description;
    }

    // Getters và setters
    public String getName() { return name; }
    public String getIconUrl() { return iconUrl; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = name; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public void setDescription(String description) { this.description = description; }
}
