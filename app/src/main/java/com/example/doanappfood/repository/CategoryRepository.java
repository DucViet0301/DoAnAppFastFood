package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.CategoryModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private ApiApp api;
    public  CategoryRepository(){
        api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    public MutableLiveData<List<CategoryModel>> getCategorys(){
        MutableLiveData<List<CategoryModel>> data = new MutableLiveData<>();
        api.getCategory().enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(Call<List<CategoryModel>> call, Response<List<CategoryModel>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<CategoryModel>> call, Throwable t) {
                data.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });
        return data;
    }
}
