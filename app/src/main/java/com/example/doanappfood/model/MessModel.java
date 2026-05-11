package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessModel {
    private boolean success;
    private String message;
    @SerializedName("order_id")
    private int order_id;

    public int getOrder_id() {
        return order_id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
