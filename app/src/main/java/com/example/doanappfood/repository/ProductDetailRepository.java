package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.model.SaucesModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailRepository {
    private ApiApp apiApp;
    public ProductDetailRepository() {
        apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }

    public MutableLiveData<ProductDetailModel> getProductDetail(int id){
        MutableLiveData<ProductDetailModel> data = new MutableLiveData<>();

        apiApp.getProductDetail(id).enqueue(new Callback<List<ProductDetailModel>>() {
            @Override
            public void onResponse(Call<List<ProductDetailModel>> call, Response<List<ProductDetailModel>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    data.setValue(response.body().get(0));
                }
            }

            @Override
            public void onFailure(Call<List<ProductDetailModel>> call, Throwable t) {
                data.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });

        return data;
    }
}
