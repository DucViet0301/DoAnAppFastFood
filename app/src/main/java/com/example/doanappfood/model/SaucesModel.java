package com.example.doanappfood.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.example.doanappfood.BR;
import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.util.Locale;

// 1. Phải kế thừa BaseObservable để có hàm notifyPropertyChanged
public class SaucesModel extends BaseObservable {
    int id, idProduct;
    String name, image;

    @SerializedName("price")
    String price;

    private boolean isSelected = false;
    private int quantity = 0;


    @Bindable
    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @Bindable
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    public int getIdProduct() { return idProduct; }
    public void setIdProduct(int idProduct) { this.idProduct = idProduct; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @Bindable
    public boolean isSelected() {
        return quantity > 0;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }

    public double getPriceValue() {
        try {
            return Double.parseDouble(price);
        } catch (Exception e) {
            return 0;
        }
    }
}