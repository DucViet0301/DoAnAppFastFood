package com.example.doanappfood.model;

public class CheckoutModel {
    public final double saleTotal;
    public final double listTotal;
    public final int userId;
    public final String cartJson;
    public CheckoutModel(double saleTotal, double listTotal, int userId, String cartJson) {
        this.saleTotal = saleTotal;
        this.listTotal = listTotal;
        this.userId = userId;
        this.cartJson = cartJson;
    }
}
