package com.example.doanappfood.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.repository.AuthRepository;
import com.example.doanappfood.viewmodel.ChangepasswordViewModel;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private AppCompatButton btnSubmitChange;
    private EditText edtOldPass, edtNewPass, edtConfirmPass;
    private ChangepasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initViews();
        setupViewModel();
        setupClickListeners();
    }
    private void initViews() {
        btnBack         = findViewById(R.id.btnBackChange);
        btnSubmitChange = findViewById(R.id.btnSubmitChange);
        edtOldPass      = findViewById(R.id.edtOldPass);
        edtNewPass      = findViewById(R.id.edtNewPass);
        edtConfirmPass  = findViewById(R.id.edtConfirmPass);
    }
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ChangepasswordViewModel.class);

        viewModel.getIsLoading().observe(this, loading -> {
            btnSubmitChange.setEnabled(!Boolean.TRUE.equals(loading));
            btnSubmitChange.setText(Boolean.TRUE.equals(loading)
                    ? "Đang xử lý..." : "Xác nhận Đổi mật khẩu");
        });

        viewModel.getChangeSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                navigateToLogin();
            }
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        viewModel.getFieldError().observe(this, field -> {
            if (field == null) return;
            switch (field) {
                case "old":     edtOldPass.setError("Nhập mật khẩu cũ");              break;
                case "new":     edtNewPass.setError("Nhập mật khẩu mới");             break;
                case "confirm": edtConfirmPass.setError("Mật khẩu xác nhận không khớp"); break;
            }
        });
    }
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSubmitChange.setOnClickListener(v -> viewModel.changePassword(
                edtOldPass.getText().toString().trim(),
                edtNewPass.getText().toString().trim(),
                edtConfirmPass.getText().toString().trim()
        ));
    }
    private void navigateToLogin() {
        new SessionManager(this).logout();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}
