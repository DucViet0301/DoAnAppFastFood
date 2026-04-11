package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

public class SaucesModel {
    int id, idProduct;
    String name, image;
    @SerializedName("price")
    String price;

    public String getPrice() {
        return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
