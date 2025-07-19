package com.example.tourbooking.model;

import java.io.Serializable;

public class Insurance implements Serializable {
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
