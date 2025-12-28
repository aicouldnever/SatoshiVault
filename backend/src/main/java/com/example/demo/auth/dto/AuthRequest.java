package com.example.demo.auth.dto;

public class AuthRequest {
    private String passphrase;
    private String password;

    public AuthRequest() {}

    public AuthRequest(String passphrase) {
        this.passphrase = passphrase;
    }

    public AuthRequest(String password, String passphrase) {
        this.password = password;
        this.passphrase = passphrase;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
