package com.example.tourbooking.model;

public class ChatThread {
    public String agentName;
    public String lastMessage;
    public String time;
    public int avatarRes;

    public ChatThread(String agentName, String lastMessage, String time, int avatarRes) {
        this.agentName = agentName;
        this.lastMessage = lastMessage;
        this.time = time;
        this.avatarRes = avatarRes;
    }
}