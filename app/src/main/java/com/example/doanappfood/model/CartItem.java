package com.example.doanappfood.model;

public class CartItem {
    private String productName;
    private String description;
    private int price;
    private int quantity;
    private String imageUrl;

    // Constructor (Hàm khởi tạo)
    public CartItem(String productName, String description, int price, int quantity, String imageUrl) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    // Các hàm Getter / Setter để lấy và sửa dữ liệu
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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
}