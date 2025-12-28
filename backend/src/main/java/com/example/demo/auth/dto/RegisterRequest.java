package com.example.demo.auth.dto;

public class RegisterRequest {
    private String password;
    private String passphrase;

    public RegisterRequest() { }

    public RegisterRequest(String password, String passphrase) {
        this.password = password;
        this.passphrase = passphrase;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
}
