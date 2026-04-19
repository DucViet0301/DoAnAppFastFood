package com.example.doanappfood.model;

public class CartSauceItem {

    private int id;
    private int cartItemId;
    private String name;

    public CartSauceItem(int id, int cartItemId, String name) {
        this.id = id;
        this.cartItemId = cartItemId;
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public String getName() {
        return name;
    }
}
