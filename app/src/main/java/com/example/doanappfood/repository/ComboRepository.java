package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.ComboModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComboRepository {
    private ApiApp api;
    public  ComboRepository(){
        api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    public MutableLiveData<List<ComboModel>> getCombos(){
        MutableLiveData<List<ComboModel>> data = new MutableLiveData<>();
        api.getCombos().enqueue(new Callback<List<ComboModel>>() {
            @Override
            public void onResponse(Call<List<ComboModel>> call, Response<List<ComboModel>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<ComboModel>> call, Throwable t) {
                data.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });
        return data;
    }
}
