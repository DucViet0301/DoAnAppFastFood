package com.example.doanappfood.model;

public class NotificationModel {
    private int orderId;
    private String title;
    private String message;
    private String time;
    private String totalPrice;
    private String status;
    private String type;

    public NotificationModel(int orderId, String title, String message, String time, String totalPrice, String status, String type) {
        this.orderId = orderId;
        this.title = title;
        this.message = message;
        this.time = time;
        this.totalPrice = totalPrice;
        this.status = status;
        this.type = type;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }
}
