package com.example.doanappfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.doanappfood.R;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private ImageView btnBack;
    private EditText edtEmail;
    private AppCompatButton btnGetOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btnBackForgot);
        edtEmail = findViewById(R.id.edtEmailForgot);
        btnGetOTP = findViewById(R.id.btnGetOTP);

        btnBack.setOnClickListener(v -> finish());

        btnGetOTP.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            // 1. Kiểm tra xem người dùng có để trống không
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Khóa nút
            btnGetOTP.setEnabled(false);
            Toast.makeText(ForgotPasswordActivity.this, "Đang kiểm tra thông tin...", Toast.LENGTH_SHORT).show();

            // 2. Gọi API lên Server
            ApiApp apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);
            apiApp.forgotPasswordSendOtp(email).enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    btnGetOTP.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            // Thành công: Email có tồn tại và đã gửi mã OTP
                            Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class);


                            intent.putExtra("EMAIL_FORGOT", email);

                            startActivity(intent);
                            finish();
                        } else {
                            // Thất bại: Email chưa đăng ký hoặc lỗi
                            Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    btnGetOTP.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}