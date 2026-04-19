package com.example.doanappfood.Utlis;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationPermissionHelper {

    private static final int REQUEST_CODE = 101;
    private final AppCompatActivity activity;

    public LocationPermissionHelper(AppCompatActivity activity) {
        this.activity = activity;
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE);
    }

    public boolean isGrantedResult(int requestCode, int[] grantResults) {
        return requestCode == REQUEST_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }
}
