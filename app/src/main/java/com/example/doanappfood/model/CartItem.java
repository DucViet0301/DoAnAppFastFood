package com.example.doanappfood.model;

public class CartItem {
    public int    ProudctId;
    public String name;
    public double list_price, sale_price;
    public int    quantity;
    public String imageUrl;

    public CartItem(int proudctId, String name, double list_price, double sale_price, int quantity, String imageUrl) {
        ProudctId = proudctId;
        this.name = name;
        this.list_price = list_price;
        this.sale_price = sale_price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public double getSubtotal(){
        if (sale_price > 0 && sale_price < list_price){
            return sale_price * quantity;
        }
        return list_price * quantity;
    }
}
