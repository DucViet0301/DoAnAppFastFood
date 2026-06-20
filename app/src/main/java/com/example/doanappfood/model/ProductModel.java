package com.example.doanappfood.model;

public class ProductModel {
    int id;
    String image, name;
    double list_price, sale_price;
    int quantity;


    public ProductModel() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductModel(int id, String image, String name, double list_price, double sale_price) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.list_price = list_price;
        this.sale_price = sale_price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public double getList_price() {
        return list_price;
    }

    public void setList_price(double list_price) {
        this.list_price = list_price;
    }

    public double getSale_price() {
        return sale_price;
    }

    public void setSale_price(double sale_price) {
        this.sale_price = sale_price;
    }
}
