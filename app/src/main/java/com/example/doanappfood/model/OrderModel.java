package com.example.doanappfood.model;

public class OrderModel {
    private  int id, time;
    private String total_price, address, created_at, status, total_items;

    public OrderModel(int id, int time, String total_price, String address, String created_at, String status, String total_items) {
        this.id = id;
        this.time = time;
        this.total_price = total_price;
        this.address = address;
        this.created_at = created_at;
        this.status = status;
        this.total_items = total_items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotal_items() {
        return total_items;
    }

    public void setTotal_items(String total_items) {
        this.total_items = total_items;
    }
}
