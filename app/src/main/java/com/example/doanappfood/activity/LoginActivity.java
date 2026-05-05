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
import androidx.lifecycle.ViewModelProvider;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ImageView btnClose;
    private AppCompatButton btnLogin;
    private TextView tvCreateAccount;
    private EditText edtLogin, edtPassword;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        // Khởi tạo ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initView();
        observeViewModel();
        String expiredMsg = getIntent().getStringExtra("expired_message");
        if (expiredMsg != null) {
            Toast.makeText(this, expiredMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void initView() {
        btnClose = findViewById(R.id.btnCloseLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        edtLogin = findViewById(R.id.edtLogin);       // SĐT hoặc email
        edtPassword = findViewById(R.id.edtPassword);    // Mật khẩu

        // Nút đóng
        btnClose.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Chuyển sang màn hình đăng ký
        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        // Nút đăng nhập
        btnLogin.setOnClickListener(v -> {
            String login = edtLogin.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            // Validate
            if (login.isEmpty()) {
                edtLogin.setError("Vui lòng nhập số điện thoại hoặc email");
                return;
            }
            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }

            // Gọi ViewModel
            authViewModel.login(login, password);
        });
    }

    private void observeViewModel() {
        // Quan sát trạng thái loading
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                btnLogin.setEnabled(false);
                btnLogin.setText("Đang xử lý...");
            } else {
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng nhập");
            }
        });

        // Quan sát kết quả thành công
        authViewModel.getAuthResult().observe(this, authResponse -> {
            if (authResponse != null && authResponse.isSuccess()) {
                // Lưu token + thông tin user vào SharedPreferences
                SessionManager session = new SessionManager(this);
                session.saveSession(
                        authResponse.getToken(),
                        authResponse.getRefreshToken(), // Lưu thêm Refresh Token ở đây
                        authResponse.getUser().getId(),
                        authResponse.getUser().getName(),
                        authResponse.getUser().getPhone(),
                        authResponse.getUser().getEmail(),
                        authResponse.getUser().getAddress(),
                        authResponse.getUser().getBirth_date()
                );

                Toast.makeText(this,
                        "Chào mừng " + authResponse.getUser().getName(),
                        Toast.LENGTH_SHORT).show();

                // Chuyển sang MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        // Quan sát lỗi
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}