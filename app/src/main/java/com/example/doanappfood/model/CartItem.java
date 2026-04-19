package com.example.doanappfood.model;

import java.util.List;

public class CartItem {
    public int id;
    public  int userId;
    public int productId;
    public String name;
    public double list_price, sale_price;
    public int quantity;
    public String imageUrl;
    public List<CartSauceItem> sauces;
    public String created_at;
    public  String comboDetail;

    public CartItem(int id, int userId, int productId, String name,
                    double list_price, double sale_price, int quantity,
                    String imageUrl, List<CartSauceItem> sauces,
                    String comboDetail, String created_at) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.name = name;
        this.list_price = list_price;
        this.sale_price = sale_price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.sauces = sauces;
        this.comboDetail = comboDetail;
        this.created_at = created_at;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<CartSauceItem> getSauces() {
        return sauces;
    }

    public void setSauces(List<CartSauceItem> sauces) {
        this.sauces = sauces;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getComboDetail() {
        return comboDetail;
    }

    public void setComboDetail(String comboDetail) {
        this.comboDetail = comboDetail;
    }

    public double getSubtotal(){
        if (sale_price > 0 && sale_price < list_price){
            return sale_price * quantity;
        }
        return list_price * quantity;
    }
}
