package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.model.OrderModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private ApiApp apiApp;
    MutableLiveData<MessModel> data;

    public OrderRepository() {
        apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);
        data = new MutableLiveData<>();
    }

    public void checkOut(String detail) {
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                detail
        );

        apiApp.postOrder(body).enqueue(new Callback<MessModel>() {
            @Override
            public void onResponse(Call<MessModel> call, Response<MessModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_DEBUG", "Raw message: " + response.body().getMessage());
                    Log.d("API_DEBUG", "Raw status " + response.body().getOrder_id());
                }
                if (response.code() == 201 || response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    MessModel error = new MessModel();
                    error.setSuccess(false);
                    error.setMessage("Đặt hàng thất bại!");
                    data.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<MessModel> call, Throwable t) {
                Log.e("CHECKOUT_ERROR", t.getMessage());
                MessModel error = new MessModel();
                error.setSuccess(false);
                error.setMessage("Lỗi kết nối!");
                data.postValue(error);
            }
        });
    }

    public MutableLiveData<MessModel> messModelMutableLiveData() {
        return data;
    }

    public MutableLiveData<List<OrderModel>> getOrder(int userId) {
        MutableLiveData<List<OrderModel>> data = new MutableLiveData<>();
        apiApp.getAllOrder(userId).enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                data.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });
        return data;
    }
}
