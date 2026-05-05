package com.example.doanappfood.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.model.UserModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private ImageView btnBack;
    private AppCompatButton btnRegister;
    private EditText edtName, edtEmail, edtPhone, edtPassword, edtDob;
    private CheckBox cbTerms;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        btnBack = findViewById(R.id.btnBack);
        btnRegister = findViewById(R.id.btnRegister);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtDob = findViewById(R.id.edtDob);
        cbTerms = findViewById(R.id.cbTerms);
        tvLogin = findViewById(R.id.tvLogin);

        checkInputs();

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { checkInputs(); }
            @Override public void afterTextChanged(Editable s) {}
        };

        edtName.addTextChangedListener(textWatcher);
        edtEmail.addTextChangedListener(textWatcher);
        edtPhone.addTextChangedListener(textWatcher);
        edtPassword.addTextChangedListener(textWatcher);
        edtDob.addTextChangedListener(textWatcher);

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> checkInputs());

        btnBack.setOnClickListener(v -> finish());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String dob = edtDob.getText().toString().trim();

            registerApi(name, email, phone, password, dob);
        });
    }

    private void registerApi(String name, String email, String phone, String password, String dob) {
        Toast.makeText(this, "Đang gửi dữ liệu...", Toast.LENGTH_SHORT).show();
        btnRegister.setEnabled(false);

        ApiApp apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);

        UserModel user = new UserModel(name, email, phone, password, dob);

        apiApp.registerUser(user).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        btnRegister.setEnabled(true);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Lỗi Server rồi!", Toast.LENGTH_LONG).show();
                    btnRegister.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_LONG).show();
                btnRegister.setEnabled(true);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }

    private void checkInputs() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        boolean isChecked = cbTerms.isChecked();

        if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty() && !password.isEmpty() && !dob.isEmpty() && isChecked) {
            btnRegister.setEnabled(true);
            btnRegister.setBackgroundResource(R.drawable.bg_button_login);
        } else {
            btnRegister.setEnabled(false);
            btnRegister.setBackgroundResource(R.drawable.button_background_gray);
        }
    }
}