package com.example.tourbooking.model;

import java.io.Serializable;

public class User implements Serializable {
    // Tên các biến phải khớp chính xác với tên trường trên Firebase
    public String avatarUrl;
    public String dob;
    public String fullName;
    public String gender;
    public String phoneNumber;
    public String userId;
    public String userName;

    public User() {

    }

    public User(String avatarUrl, String dob, String fullName, String gender, String phoneNumber, String userId, String userName) {
        this.avatarUrl = avatarUrl;
        this.dob = dob;
        this.fullName = fullName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.userName = userName;
    }

    // Getters
    public String getAvatarUrl() { return avatarUrl; }
    public String getDob() { return dob; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}