package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;
import com.example.doanappfood.model.BannerModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerRepository {
    private ApiApp api;

    public BannerRepository() {
        api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    public MutableLiveData<List<BannerModel>> getBanner(){
        MutableLiveData<List<BannerModel>> data = new MutableLiveData<>();
        api.getBanner().enqueue(new Callback<List<BannerModel>>() {
            @Override
            public void onResponse(Call<List<BannerModel>> call, Response<List<BannerModel>> response) {
                Log.d("BannerRepo", "Code: " + response.code());
                Log.d("BannerRepo", "Body: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("BannerRepo", "Size: " + response.body().size());
                    data.setValue(response.body());
                } else {
                    Log.e("BannerRepo", "Response lỗi hoặc null");
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<BannerModel>> call, Throwable t) {
                Log.d("logg", t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

}
