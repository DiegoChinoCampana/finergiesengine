package com.qip.jpa.services.auth;

public class AuthResponse {
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

    public AuthResponse(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    // getter
}

