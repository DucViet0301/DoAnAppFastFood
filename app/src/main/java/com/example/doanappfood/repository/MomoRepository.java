package com.example.doanappfood.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MomoRepository {
    private ApiApp apiApp;
    MutableLiveData<MessModel> data;

    public MomoRepository() {
        apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);
        data = new MutableLiveData<>();
    }

    public void createPayment(long amount, String orderInfo, String redirectUrl) {
        JSONObject json = new JSONObject();
        try {
            json.put("amount", amount);
            json.put("orderInfo", orderInfo);
            json.put("redirectUrl", redirectUrl);
        } catch (JSONException e) {
            MessModel error = new MessModel();
            error.setSuccess(false);
            error.setMessage("Lỗi tạo request!");
            data.postValue(error);
            return;
        }

        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );

        apiApp.createMomoPayment(body).enqueue(new Callback<MessModel>() {
            @Override
            public void onResponse(Call<MessModel> call, Response<MessModel> response) {
                Log.e("MOMO_RESPONSE", "code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    MessModel result = response.body();
                    // LOG CHI TIẾT — xem server trả về gì
//                    Log.e("MOMO_BODY", "resultCode=" + result.getResultCode()
//                            + " | payUrl=" + result.getPayUrl()
//                            + " | orderId=" + result.getOrderId()
//                            + " | message=" + result.getMessage()
//                            + " | isSuccess=" + result.isSuccess());
                    data.postValue(result);
                } else {
                    // Log raw response body khi lỗi
                    try {
                        String raw = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("MOMO_ERROR_BODY", "raw: " + raw);
                    } catch (Exception e) { e.printStackTrace(); }
                    MessModel error = new MessModel();
                    error.setSuccess(false);
                    error.setMessage("Tạo đơn thanh toán thất bại!");
                    data.postValue(error);
                }
            }

            @Override
            public void onFailure(Call<MessModel> call, Throwable t) {
                Log.e("MOMO_ERROR", t.getMessage());
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
}