package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderDetailModel {
    @SerializedName("id")
    private int id;

    @SerializedName("total_price")
    private double total_price;

    @SerializedName("address")
    private String address;

    @SerializedName("status")
    private String status;

    @SerializedName("is_dungcu")
    private  Boolean is_dungcu;
    @SerializedName("is_tuongca")
    private  Boolean is_tuongca;
    @SerializedName("is_tuongot")
    private  Boolean is_tuongot;

    @SerializedName("note")
    private String note;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("customer_name")
    private String customer_name;

    @SerializedName("phone_number")
    private String phone_number;

    @SerializedName("updated_at")
    private String updated_at;

    @SerializedName("products")
    private List<ProductDetailModel> products;



    public int getId()                          { return id; }
    public double getTotal_price()              { return total_price; }
    public String getAddress()                  { return address; }
    public String getStatus()                   { return status; }
    public String getNote()                     { return note; }
    public String getCreated_at()               { return created_at; }
    public String getCustomer_name()            { return customer_name; }
    public String getPhone_number()             { return phone_number; }
    public List<ProductDetailModel> getProducts() { return products; }
    public Boolean getIs_tuongot() {
        return is_tuongot;
    }

    public Boolean getIs_tuongca() {
        return is_tuongca;
    }

    public Boolean getIs_dungcu() {
        return is_dungcu;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}