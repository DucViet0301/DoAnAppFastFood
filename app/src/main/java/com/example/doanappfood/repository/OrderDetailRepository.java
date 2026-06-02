package com.example.doanappfood.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.model.OrderDetailModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;
import com.example.doanappfood.viewmodel.OrderDetailViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailRepository {
    private final ApiApp apiApp;

    public OrderDetailRepository(Context context) {
        apiApp = RetrofitInstance.getRetrofit(context).create(ApiApp.class);
    }

    public LiveData<OrderDetailModel> getOrderDetail(int orderId) {
        MutableLiveData<OrderDetailModel> data = new MutableLiveData<>();
        Log.d("ORDER_DETAIL", "Gọi API với orderId=" + orderId);  // ← thêm dòng này

        apiApp.getOrderDetail(orderId).enqueue(new Callback<OrderDetailModel>() {
            @Override
            public void onResponse(Call<OrderDetailModel> call, Response<OrderDetailModel> response) {
                Log.d("ORDER_DETAIL", "HTTP code=" + response.code());  // ← thêm
                Log.d("ORDER_DETAIL", "body=" + response.body());       // ← thêm
                if (response.isSuccessful() && response.body() != null) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<OrderDetailModel> call, Throwable t) {
                Log.e("ORDER_DETAIL", "Lỗi mạng: " + t.getMessage());  // ← thêm
                data.setValue(null);
            }
        });
        return data;
    }

    public MutableLiveData<MessModel> cancel(int orderId) {
        MutableLiveData<MessModel> result = new MutableLiveData<>();
        apiApp.cancelOrder(orderId).enqueue(new Callback<MessModel>() {
            @Override
            public void onResponse(Call<MessModel> call, Response<MessModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(response.body());
                } else {
                    MessModel mess = new MessModel();
                    mess.setSuccess(false);
                    try {
                        String error = response.errorBody().string();
                        if (error.contains("Đã quá 15 phút")) {
                            mess.setMessage("Đã quá 15 phút bạn không thể hủy đơn");
                        } else {
                            mess.setMessage("Huỷ đơn thất bại");
                        }
                    } catch (Exception e) {
                        mess.setMessage("Huỷ đơn thất bại");
                    }
                    result.setValue(mess);
                }
            }

            @Override
            public void onFailure(Call<MessModel> call, Throwable t) {
                result.setValue(null);
                Log.d("logg", t.getMessage());
            }
        });
        return result;
    }
}
