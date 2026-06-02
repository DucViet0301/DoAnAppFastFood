package com.example.doanappfood.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.model.OrderModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private ApiApp apiApp;
    MutableLiveData<MessModel> data;

    public OrderRepository(Context context) {
        apiApp = RetrofitInstance.getRetrofit(context).create(ApiApp.class);
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
                    try {
                        String errorJson = response.errorBody().string();
                        MessModel errorMess = new Gson().fromJson(errorJson, MessModel.class);
                        data.postValue(errorMess);
                    } catch (Exception e) {
                        MessModel fallback = new MessModel();
                        fallback.setSuccess(false);
                        fallback.setMessage("Đặt hàng thất bại, thử lại sau");
                        data.postValue(fallback);
                    }
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
                Log.d("HISTORY", "HTTP code = " + response.code());         // ← thêm
                Log.d("HISTORY", "body = " + response.body());              // ← thêm
                Log.d("HISTORY", "size = " + (response.body() != null ? response.body().size() : "null")); // ← thêm
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                Log.e("HISTORY", "Lỗi: " + t.getMessage());                // ← thêm
                data.setValue(null);
            }
        });
        return data;
    }
}
