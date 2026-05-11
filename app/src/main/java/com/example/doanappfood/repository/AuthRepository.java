package com.example.doanappfood.repository;

import android.content.Context;

import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.model.AuthModel;
import com.example.doanappfood.model.ChangePassModel;
import com.example.doanappfood.model.LoginModel;
import com.example.doanappfood.model.RegisterModel;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiApp api;
    private final SessionManager sessionManager; // ← THÊM KHAI BÁO NÀY

    public AuthRepository(Context context) {
        api = RetrofitInstance.getRetrofit(context).create(ApiApp.class);
        sessionManager = new SessionManager(context); // ← THÊM KHỞI TẠO NÀY
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

    public void logout(AuthCallback callback) {
        api.logout().enqueue(new Callback<AuthModel>() {
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
                        String errorStr = response.errorBody().string();
                        org.json.JSONObject json = new org.json.JSONObject(errorStr);
                        callback.onFailure(json.optString("error", "Đăng xuất thất bại"));
                    } catch (Exception e) {
                        callback.onFailure("Đăng xuất thất bại");
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthModel> call, Throwable t) {
                callback.onFailure("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Đổi mật khẩu
    public void changePassword(String oldPassword, String newPassword, ResponseCallback callback) {
        // THÊM: Kiểm tra token có null không
        String tokenCheck = sessionManager.getToken();
        android.util.Log.e("CHANGE_PASS", "Token hiện tại: " + tokenCheck);

        ChangePassModel request = new ChangePassModel(oldPassword, newPassword);

        api.changePassword(request).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseModel resBody = response.body();
                    if (resBody.isSuccess()) {
                        callback.onSuccess(resBody);
                    } else {
                        String msg = resBody.getMessage() != null ? resBody.getMessage() : "Mật khẩu cũ không chính xác";
                        callback.onFailure(msg);
                    }
                } else {
                    try {
                        String errorStr = response.errorBody().string();
                        android.util.Log.e("CHANGE_PASS", "Code: " + response.code() + " | Body: " + errorStr); // ← THÊM DÒNG NÀY
                        org.json.JSONObject json = new org.json.JSONObject(errorStr);
                        String serverError = json.optString("error", json.optString("message", "Lỗi không xác định"));
                        callback.onFailure(serverError);
                    } catch (Exception e) {
                        android.util.Log.e("CHANGE_PASS", "Parse error: " + e.getMessage()); // ← THÊM DÒNG NÀY
                        callback.onFailure("Lỗi không xác định");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                callback.onFailure("Lỗi kết nối server");
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(AuthModel response);

        void onFailure(String errorMessage);
    }

    public interface ResponseCallback {
        void onSuccess(ResponseModel response);

        void onFailure(String errorMessage);
    }
}