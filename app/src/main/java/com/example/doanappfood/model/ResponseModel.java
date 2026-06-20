package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

public class ResponseModel {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("error")
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message != null ? message : error;
    }

    public String getError() {
        return error;
    }
}
