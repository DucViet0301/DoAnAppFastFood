package com.example.doanappfood.Utlis;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doanappfood.model.DirectionModel;
import com.example.doanappfood.model.StoreModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FusedLocationHelper {
    private ApiApp api;
    public  interface  OnAddressCallback{
        void onSuccess(String address);
        void onFailure(String errorMessage);
    }
    public interface OnLocationCallback {
        void onSuccess(double lat, double lng);
        void onFailure(String message);
    }
    public interface OnRouteCallback {
        void onSuccess(String storeName, String distance, String duration);
        void onFailure(String message);
    }
    private  final AppCompatActivity activity;
    private  final FusedLocationProviderClient fusedClient;

    public  FusedLocationHelper(AppCompatActivity activity){
        this.activity = activity;
        this.fusedClient = LocationServices.getFusedLocationProviderClient(activity);
        this.api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    @SuppressLint("MissingPermission")
    public  void fetchAddress(OnAddressCallback callback){
       fusedClient.getLastLocation().addOnSuccessListener(activity, location -> {
           if(location != null){
               LocationHelper.getAddressFromLatLng(
                       activity,
                       location.getLatitude(),
                       location.getLongitude(),
                       new LocationHelper.LocationCallback() {
                           @Override
                           public void onAddressRetrieved(String address) {
                               callback.onSuccess(address);
                           }

                           @Override
                           public void onError(String errorMessage) {
                               callback.onFailure(errorMessage);
                           }
                       }
               );
           }else{
               callback.onFailure("Không tìm thấy vị trí");
           }
       });
    }
    @SuppressLint("MissingPermission")
    public void fetchLocation(OnLocationCallback callback) {
        fusedClient.getLastLocation().addOnSuccessListener(activity, location -> {
            if (location != null) {
                callback.onSuccess(location.getLatitude(), location.getLongitude());
            } else {
                callback.onFailure("Không tìm thấy vị trí. Hãy bật GPS!");
            }
        });
    }

    public void getNearStore(double userLat, double userLng, OnRouteCallback callback) {
        api.getNearestStore(userLat, userLng).enqueue(new Callback<StoreModel>() {
            @Override
            public void onResponse(Call<StoreModel> call, Response<StoreModel> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onFailure("Không tìm thấy cửa hàng gần bạn");
                    return;
                }
                StoreModel nearest = response.body();

                api.getDirections(userLat, userLng, nearest.getLat(), nearest.getLng())
                        .enqueue(new Callback<DirectionModel>() {
                            @Override
                            public void onResponse(Call<DirectionModel> call, Response<DirectionModel> response) {
                                if (!response.isSuccessful() || response.body() == null) {
                                    callback.onFailure("Không lấy được đường đi");
                                    return;
                                }
                                DirectionModel dir = response.body();
                                callback.onSuccess(
                                        nearest.getName(),
                                        dir.getDistanceText(), // lay quang duong
                                        dir.getDurationText()  // lay thoi gian
                                );
                            }
                            @Override
                            public void onFailure(Call<DirectionModel> call, Throwable t) {
                                callback.onFailure("Lỗi mạng: " + t.getMessage());
                            }
                        });
            }
            @Override
            public void onFailure(Call<StoreModel> call, Throwable t) {
                callback.onFailure("Lỗi mạng: " + t.getMessage());
            }
        });
    }
}
