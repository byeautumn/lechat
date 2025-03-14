package com.snowyowl.lechat.websocket.view;

public class LoginView {
    private String username;
    private String password;

    // Constructor, getters, and setters
    public LoginView() {}

    public LoginView(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
