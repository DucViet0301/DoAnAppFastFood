package com.example.doanappfood.model;

public class ResponseModel {
    private boolean success;
    private String message;
    private UserModel user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserModel getUser() { return user; }
}