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
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.repository.AuthRepository;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private AppCompatButton btnSubmitChange;
    private EditText edtOldPass, edtNewPass, edtConfirmPass;
    private AuthRepository authRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        authRepository = new AuthRepository(this);
        sessionManager = new SessionManager(this);

        initView();
        setupListeners();
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBackChange);
        btnSubmitChange = findViewById(R.id.btnSubmitChange);
        edtOldPass = findViewById(R.id.edtOldPass);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Xác nhận đổi mật khẩu
        btnSubmitChange.setOnClickListener(v -> {
            String oldPass = edtOldPass.getText().toString().trim();
            String newPass = edtNewPass.getText().toString().trim();
            String confirmPass = edtConfirmPass.getText().toString().trim();

            if (validateInput(oldPass, newPass, confirmPass)) {
                btnSubmitChange.setEnabled(false);
                btnSubmitChange.setText("Đang xử lý...");

                authRepository.changePassword(oldPass, newPass, new AuthRepository.ResponseCallback() {
                    @Override
                    public void onSuccess(ResponseModel response) {
                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                        performLogoutAndLogin();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        btnSubmitChange.setEnabled(true);
                        btnSubmitChange.setText("Xác nhận Đổi mật khẩu");
                        Toast.makeText(ChangePasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateInput(String oldPass, String newPass, String confirmPass) {
        if (oldPass.isEmpty()) {
            edtOldPass.setError("Nhập mật khẩu cũ");
            return false;
        }
        if (newPass.isEmpty()) {
            edtNewPass.setError("Nhập mật khẩu mới");
            return false;
        }
        if (!newPass.equals(confirmPass)) {
            edtConfirmPass.setError("Mật khẩu xác nhận không khớp");
            return false;
        }
        return true;
    }

    private void performLogoutAndLogin() {
        // Chỉ xóa session đăng nhập, giữ lại giỏ hàng trong DB
        sessionManager.logout();
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
