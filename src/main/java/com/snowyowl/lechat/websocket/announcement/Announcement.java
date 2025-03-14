package com.snowyowl.lechat.websocket.announcement; // Adjust package name as needed

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "announcements")
public class Announcement {

    @Id
    private String id;
    private String text;
    private String username;
    private String timestamp;

    // Constructors (If needed)
    public Announcement() {}

    public Announcement(String text, String username, String timestamp) {
        this.text = text;
        this.username = username;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}