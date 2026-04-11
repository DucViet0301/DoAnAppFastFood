package com.example.doanappfood.model;

import com.google.gson.annotations.SerializedName;

public class DirectionModel {
    @SerializedName("distance_text")
    private String distanceText;
    @SerializedName("distance_value")
    private double distanceValue;
    @SerializedName("duration_text")
    private String durationText;
    @SerializedName("duration_value")
    private double durationValue;
    @SerializedName("polyline")
    private String polyline;

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String distanceText) {
        this.distanceText = distanceText;
    }

    public double getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(double distanceValue) {
        this.distanceValue = distanceValue;
    }

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String durationText) {
        this.durationText = durationText;
    }

    public double getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(double durationValue) {
        this.durationValue = durationValue;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }
}
