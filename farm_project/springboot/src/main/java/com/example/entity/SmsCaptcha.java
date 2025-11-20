package com.example.entity;

public class SmsCaptcha {
    private String phoneNumber;
    private String smsCode;

    // Getters 和 Setters
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return smsCode;
    }

    public void setCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
