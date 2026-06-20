package com.example.doanappfood.model;


import com.google.gson.annotations.SerializedName;

public class ChangePassModel {
    @SerializedName("oldPassword")
    private String oldPassword;
    @SerializedName("newPassword")
    private String newPassword;

    public ChangePassModel(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}

