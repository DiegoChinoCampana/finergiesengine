package com.qip.jpa.services.auth.dto;

public class RegisterRequest {
    private String email;
    private String password;
    private Long client;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getClient() {
        return client;
    }
    public void setClient(Long client) {
        this.client = client;
    }
}
