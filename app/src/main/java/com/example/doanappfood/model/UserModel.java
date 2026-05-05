package com.example.doanappfood.model;

public class UserModel {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String address;
    private String birth_date;

    public UserModel(int id, String name, String email, String phone, String password, String address, String birth_date) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.address = address;
        this.birth_date = birth_date;
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

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(String phone) {

        this.phone = phone;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getAddress() {

        return address;
    }

    public void setAddress(String address) {

        this.address = address;
    }

    public String getBirth_date() {

        return birth_date;
    }

    public void setBirth_date(String birth_date) {

        this.birth_date = birth_date;
    }
}
