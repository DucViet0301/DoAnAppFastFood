package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

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
    public void checkOut(String detail){
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                detail
        );

        apiApp.postOrder(body).enqueue(new Callback<MessModel>() {
            @Override
            public void onResponse(Call<MessModel> call, Response<MessModel> response) {
                Log.e("CHECKOUT_RESPONSE", "code: " + response.code());

                if (response.code() == 201 || response.isSuccessful()) {
                    MessModel success = new MessModel();
                    success.setSuccess(true);
                    success.setMessage("Đặt hàng thành công!");
                    data.postValue(success);
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
    public MutableLiveData<MessModel> messModelMutableLiveData(){
        return data;
    }


}
