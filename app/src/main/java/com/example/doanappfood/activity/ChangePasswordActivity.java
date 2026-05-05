package com.example.doanappfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvEmailHint;
    private AppCompatButton btnSendOTP, btnSubmitChange;
    private EditText edtOTP, edtNewPass, edtConfirmPass;
    private String userEmail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnBack = findViewById(R.id.btnBackChange);
        tvEmailHint = findViewById(R.id.tvEmailHint);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnSubmitChange = findViewById(R.id.btnSubmitChange);
        edtOTP = findViewById(R.id.edtOTP);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);


        if (getIntent() != null && getIntent().hasExtra("EMAIL_FORGOT")) {
            userEmail = getIntent().getStringExtra("EMAIL_FORGOT");
        }
        else {
            SharedPreferences sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
            userEmail = sharedPreferences.getString("userEmail", "");
        }

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy Email!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // --- KẾT THÚC ---

        // Hiển thị lên màn hình cho User biết là đang làm việc với Email nào
        tvEmailHint.setText("Mã OTP sẽ được gửi đến: " + userEmail);

        btnBack.setOnClickListener(v -> finish());

        // Bấm gửi OTP
        btnSendOTP.setOnClickListener(v -> {
            sendOtpApi();
        });

        // Bấm Xác nhận đổi Pass
        btnSubmitChange.setOnClickListener(v -> {
            String otp = edtOTP.getText().toString().trim();
            String pass = edtNewPass.getText().toString().trim();
            String confirm = edtConfirmPass.getText().toString().trim();

            if(otp.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!pass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }
            changePasswordApi(otp, pass);
        });
    }

    private void sendOtpApi() {
        Toast.makeText(this, "Đang gửi email...", Toast.LENGTH_SHORT).show();
        btnSendOTP.setEnabled(false);
        ApiApp apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);

        apiApp.sendOtp(userEmail).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                btnSendOTP.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if(response.body().isSuccess()) {
                        btnSendOTP.setText("Gửi lại mã");
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                btnSendOTP.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changePasswordApi(String otp, String newPassword) {
        Toast.makeText(this, "Đang xác thực...", Toast.LENGTH_SHORT).show();
        btnSubmitChange.setEnabled(false);
        ApiApp apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);

        apiApp.changePassword(userEmail, otp, newPassword).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                btnSubmitChange.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ChangePasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if(response.body().isSuccess()) {
                        finish();
                    }
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Lỗi Server trả về!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                btnSubmitChange.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}