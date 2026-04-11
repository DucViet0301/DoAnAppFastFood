package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.NewModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewRepository {
    private ApiApp api;

    public NewRepository() {
        api = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }
    public MutableLiveData<List<NewModel>> getNew(){
        MutableLiveData<List<NewModel>> data = new MutableLiveData<>();
        api.getNew().enqueue(new Callback<List<NewModel>>() {
            @Override
            public void onResponse(Call<List<NewModel>> call, Response<List<NewModel>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<NewModel>> call, Throwable t) {
                data.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });
        return  data;
    }
}
