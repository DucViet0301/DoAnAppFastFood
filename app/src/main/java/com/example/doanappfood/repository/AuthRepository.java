package com.example.doanappfood.repository;

import android.content.Context;
import com.example.doanappfood.model.AuthModel;

import com.example.doanappfood.model.LoginModel;
import com.example.doanappfood.model.RegisterModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiApp api;

    public AuthRepository(Context context) {
        api = RetrofitInstance.getRetrofit(context).create(ApiApp.class);
    }

    // Interface callback để trả kết quả về ViewModel
    public interface AuthCallback {
        void onSuccess(AuthModel response);

        void onFailure(String errorMessage);
    }

    // Đăng nhập
    public void login(String login, String password, AuthCallback callback) {
        LoginModel request = new LoginModel(login, password);

        api.login(request).enqueue(new Callback<AuthModel>() {
            @Override
            public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.body().getError());
                    }
                } else {
                    try {
                        // Đọc lỗi từ server (401, 400...)
                        org.json.JSONObject json = new org.json.JSONObject(
                                response.errorBody().string()
                        );
                        callback.onFailure(json.optString("error", "Đăng nhập thất bại"));
                    } catch (Exception e) {
                        callback.onFailure("Đăng nhập thất bại");
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthModel> call, Throwable t) {
                callback.onFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Đăng ký
    public void register(String name, String phone, String email,
                         String password, String address, String birthDate,
                         AuthCallback callback) {
        RegisterModel request = new RegisterModel(
                name, phone, email, password, address, birthDate
        );

        api.register(request).enqueue(new Callback<AuthModel>() {
            @Override
            public void onResponse(Call<AuthModel> call, Response<AuthModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onFailure(response.body().getError());
                    }
                } else {
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(
                                response.errorBody().string()
                        );
                        callback.onFailure(json.optString("error", "Đăng ký thất bại"));
                    } catch (Exception e) {
                        callback.onFailure("Đăng ký thất bại");
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthModel> call, Throwable t) {
                callback.onFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}