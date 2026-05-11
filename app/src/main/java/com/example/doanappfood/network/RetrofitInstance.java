package com.example.doanappfood.network;

import android.content.Context;
import android.content.Intent;

import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.activity.LoginActivity;
import com.example.doanappfood.model.AuthModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.1.3:3000/";

    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            SessionManager sessionManager = new SessionManager(context.getApplicationContext());

            OkHttpClient client = new OkHttpClient.Builder()
                // 1. Interceptor: Tự động thêm Token vào Header của mọi request
                .addInterceptor(chain -> {
                    Request.Builder newRequest = chain.request().newBuilder();
                    String token = sessionManager.getToken();
                    if (token != null) {
                        newRequest.addHeader("Authorization", "Bearer " + token);
                    }
                    return chain.proceed(newRequest.build());
                })
                // 2. Authenticator: Tự động gọi Refresh Token khi nhận lỗi 401 (Unauthorized)
                .authenticator((route, response) -> {
                    // Nếu đã thử refresh rồi mà vẫn lỗi 401 thì thôi (tránh lặp vô tận)
                    if (responseCount(response) >= 2) {
                        return null;
                    }

                    String refreshToken = sessionManager.getRefreshToken();
                    if (refreshToken == null) return null;

                    // Gọi API refresh token đồng bộ (.execute())
                    ApiApp api = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                            .create(ApiApp.class);

                    try {
                        retrofit2.Response<AuthModel> res = api.refreshToken(refreshToken).execute();
                        if (res.isSuccessful() && res.body() != null) {
                            String newToken = res.body().getToken();
                            // Lưu token mới vào máy
                            sessionManager.saveNewAccessToken(newToken);

                            // Gửi lại request bị lỗi với token mới
                            return response.request().newBuilder()
                                    .header("Authorization", "Bearer " + newToken)
                                    .build();
                        }else {
                            // THÊM ĐOẠN NÀY: Khi Refresh Token cũng hết hạn (Server trả về lỗi)

                            // 1. Xóa dữ liệu cũ
                            sessionManager.logout();

                            // 2. Chuyển hướng người dùng về màn hình Login
                            Intent intent = new Intent(context, LoginActivity.class);
                            // FLAG này giúp xóa sạch các Activity cũ, người dùng không nhấn Back lại được
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("expired_message", "Phiên đăng nhập đã hết hạn.");
                            context.startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                })
                .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Hàm đếm số lần thử lại request
    private static int responseCount(okhttp3.Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }

    // Hàm hỗ trợ cho các repository cũ chưa cập nhật Context
    public static Retrofit getRetrofit() {
        return retrofit;
    }
}
