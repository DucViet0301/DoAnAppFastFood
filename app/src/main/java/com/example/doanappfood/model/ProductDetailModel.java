package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductDetailModel {
    int id;
    String name, image;
    Double sale_price, list_price;
    @SerializedName("item_sauces")
    List<SaucesModel> saucesModel;
    @SerializedName("item_product")
    List<ProductModel> productModels;

    @SerializedName("is_combo")
    private int is_combo;

    public boolean isIs_combo() {
        return is_combo == 1;
    }
    public void setIs_combo(int is_combo) {
        this.is_combo = is_combo;
    }


    public List<ProductModel> getProductModels() {
        return productModels;
    }

    public void setProductModels(List<ProductModel> productModels) {
        this.productModels = productModels;
    }

    public List<SaucesModel> getSaucesModel() {
        return saucesModel;
    }

    public void setSaucesModel(List<SaucesModel> saucesModel) {
        this.saucesModel = saucesModel;
    }

    public Double getList_price() {
        return list_price;
    }

    public void setList_price(Double list_price) {
        this.list_price = list_price;
    }

    public Double getSale_price() {
        return sale_price;
    }

    public void setSale_price(Double sale_price) {
        this.sale_price = sale_price;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
