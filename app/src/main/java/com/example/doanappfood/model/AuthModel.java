package com.example.doanappfood.model;

public class AuthModel {
    private boolean success;
    private String message;
    private String token;
    private String refresh_token;
    private String error;
    private UserModel user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public String getError() {
        return error;
    }

    public UserModel getUser() {
        return user;
    }
}
