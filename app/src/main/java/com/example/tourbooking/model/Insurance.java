package com.example.tourbooking.model;

public class Insurance {
    private String name;
    private String provider;
    private double price;

    public Insurance(String name, String provider, double price) {
        this.name = name;
        this.provider = provider;
        this.price = price;
    }

    public String getName() { return name; }
    public String getProvider() { return provider; }
    public double getPrice() { return price; }
}
