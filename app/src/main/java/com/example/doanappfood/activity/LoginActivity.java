package com.example.doanappfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.model.LoginRequest;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ImageView btnClose;
    private AppCompatButton btnLogin;
    private TextView tvCreateAccount, tvForgotPass;
    private EditText edtAccount, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        btnClose = findViewById(R.id.btnCloseLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPass = findViewById(R.id.tvForgotPass);
        edtAccount = findViewById(R.id.edtAccount);
        edtPassword = findViewById(R.id.edtPassword);

        checkInputs();
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        edtAccount.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
        tvForgotPass.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtAccount.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            loginApi(email, password);
        });

        btnClose.setOnClickListener(v -> finish());
    }

    private void checkInputs() {
        String email = edtAccount.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            btnLogin.setEnabled(true);
            btnLogin.setBackgroundResource(R.drawable.bg_button_login);
        } else {
            btnLogin.setEnabled(false);
            btnLogin.setBackgroundResource(R.drawable.button_background_gray);
        }
    }

    private void loginApi(String email, String password) {
        Toast.makeText(this, "Đang kiểm tra...", Toast.LENGTH_SHORT).show();
        btnLogin.setEnabled(false);
        ApiApp apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);

        LoginRequest request = new LoginRequest(email, password);

        apiApp.loginUser(request).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        if (response.body().getUser() != null) {
                            String fullName = response.body().getUser().getName();
                            intent.putExtra("TEN_NGUOI_DUNG", fullName);

                            editor.putString("userName", fullName);
                            editor.putString("userEmail", response.body().getUser().getEmail());
                            editor.putString("userPhone", response.body().getUser().getPhone());

                            editor.putString("userDob", response.body().getUser().getDob());
                        }

                        editor.apply();

                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        btnLogin.setEnabled(true);
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi Server, vui lòng kiểm tra XAMPP!", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
                btnLogin.setEnabled(true);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}