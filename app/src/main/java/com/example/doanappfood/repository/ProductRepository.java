package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private ApiApp api;

    public ProductRepository() {
        api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    public MutableLiveData<List<ProductModel>> getProduct(int idCate){
        MutableLiveData<List<ProductModel>> data = new MutableLiveData<>();
        api.getProduct(idCate).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                data.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });
        return data;
    }
}
