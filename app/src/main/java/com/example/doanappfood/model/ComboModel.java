package com.example.doanappfood.model;

import java.util.List;

public class ComboModel {
    private  int id;
    private String name;
    private String image;
    private  double sale_price;
    private double list_price;
    private List<ComboItemModel> items;

    public ComboModel(int id, String name, String image, double sale_price, double list_price, List<ComboItemModel> items) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.sale_price = sale_price;
        this.list_price = list_price;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getSale_price() {
        return sale_price;
    }

    public void setSale_price(double sale_price) {
        this.sale_price = sale_price;
    }

    public double getList_price() {
        return list_price;
    }

    public void setList_price(double list_price) {
        this.list_price = list_price;
    }

    public List<ComboItemModel> getItems() {
        return items;
    }

    public void setItems(List<ComboItemModel> items) {
        this.items = items;
    }
}
