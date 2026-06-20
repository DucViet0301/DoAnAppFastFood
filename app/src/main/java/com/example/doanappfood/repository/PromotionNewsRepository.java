package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;


import com.example.doanappfood.model.PromotionNewsModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromotionNewsRepository {
    private ApiApp api;

    public PromotionNewsRepository() {
        api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    public MutableLiveData<List<PromotionNewsModel>> getPromotionNews(){
        MutableLiveData<List<PromotionNewsModel>> data = new MutableLiveData<>();
        api.getPromotionNews().enqueue(new Callback<List<PromotionNewsModel>>() {
            @Override
            public void onResponse(Call<List<PromotionNewsModel>> call, Response<List<PromotionNewsModel>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<PromotionNewsModel>> call, Throwable t) {
                data.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });
        return  data;
    }
}
