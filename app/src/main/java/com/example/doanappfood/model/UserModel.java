package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    private String name;
    private String email;
    private String phone;
    private String password;

    @SerializedName("birth_date")
    private String dob;

    // chức năng Đăng nhập
    public UserModel(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // chức năng Đăng ký
    public UserModel(String name, String email, String phone, String password, String dob) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.dob = dob;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getDob() { return dob; }
}