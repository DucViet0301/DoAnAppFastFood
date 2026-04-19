package com.example.doanappfood.model;

public class PromotionNewsModel {
    private int id;
    private String image;
    private String title;
    private String description;
    private String date;
    private String created_at;
    private String status;

    public PromotionNewsModel(int id, String image, String title, String description, String date, String created_at, String status) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.date = date;
        this.created_at = created_at;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = this.description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}