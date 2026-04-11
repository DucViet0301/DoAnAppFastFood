package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

public class StoreModel {
    private  int id;
    private  String name;
    private double lat, lng;
    @SerializedName("distance_km")
    private double distanceKm;

    public StoreModel(double distanceKm, double lng, double lat, String name, int id) {
        this.distanceKm = distanceKm;
        this.lng = lng;
        this.lat = lat;
        this.name = name;
        this.id = id;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }
    public String getFormattedDistance() {
        if (distanceKm < 1) {
            return String.format("%.0f m", distanceKm * 1000);
        }
        return String.format("%.1f km", distanceKm);
    }
}
