package com.adigastudio.kodesoalguru.models;

import android.util.Patterns;

public class Login {
    private String email;
    private String password;
    private boolean success;
    private String message;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Login(String password) {
        this.password = password;
    }

    public Login(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isEmailValid() {
        return Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches();
    }


    public boolean isPasswordLengthGreaterThan6() {
        return getPassword().length() >= 6;
    }
}
