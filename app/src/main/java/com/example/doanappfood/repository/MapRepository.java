package com.example.doanappfood.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.DirectionModel;
import com.example.doanappfood.model.StoreModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapRepository {
    private final ApiApp api;

    public MapRepository() {
        api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    public  MutableLiveData<List<StoreModel>> getALLStores(){
        MutableLiveData<List<StoreModel>> data = new MutableLiveData<>();
        api.getAllStores().enqueue(new Callback<List<StoreModel>>() {
            @Override
            public void onResponse(Call<List<StoreModel>> call, Response<List<StoreModel>> response) {
                if (response.isSuccessful()) data.setValue(response.body());
            }
            @Override
            public void onFailure(Call<List<StoreModel>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
    public MutableLiveData<StoreModel> getNearestStore(double lat, double lng) {
        MutableLiveData<StoreModel> data = new MutableLiveData<>();
        api.getNearestStore(lat, lng).enqueue(new Callback<StoreModel>() {
            @Override
            public void onResponse(Call<StoreModel> call, Response<StoreModel> response) {
                if (response.isSuccessful()) data.setValue(response.body());
            }
            @Override
            public void onFailure(Call<StoreModel> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
    public MutableLiveData<DirectionModel> getDirections(
            double fromLat, double fromLng, double toLat, double toLng) {
        MutableLiveData<DirectionModel> data = new MutableLiveData<>();
        api.getDirections(fromLat, fromLng, toLat, toLng)
                .enqueue(new Callback<DirectionModel>() {
                    @Override
                    public void onResponse(Call<DirectionModel> call, Response<DirectionModel> response) {
                        if (response.isSuccessful()) data.setValue(response.body());
                    }
                    @Override
                    public void onFailure(Call<DirectionModel> call, Throwable t) {
                        data.setValue(null);
                    }
                });
        return data;
    }
}
