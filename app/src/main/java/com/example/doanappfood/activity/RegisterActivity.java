package com.example.doanappfood.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.viewmodel.AuthViewModel;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity {

    private ImageView btnBack;
    private AppCompatButton btnRegister;
    private EditText edtName, edtEmail, edtPhone, edtPassword, edtBirthdate, edtAddress;
    private android.widget.CheckBox cbTerms;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initView();
        observeViewModel();
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBack);
        btnRegister = findViewById(R.id.btnRegister);
        edtBirthdate = findViewById(R.id.edtBirthdate);
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtAddress = findViewById(R.id.edtAddress);
        cbTerms = findViewById(R.id.cbTerms);

        btnBack.setOnClickListener(v -> finish());

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnRegister.setBackgroundResource(R.drawable.bg_button_login);
            } else {
                btnRegister.setBackgroundResource(R.drawable.button_background_gray);
            }
        });

        edtBirthdate.setOnClickListener(v -> showDatePickerDialog());

        btnRegister.setOnClickListener(v -> {
            if (!cbTerms.isChecked()) {
                Toast.makeText(this, "Vui lòng đồng ý với các điều khoản để tiếp tục",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String birthdate = edtBirthdate.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();

            boolean isValid = true;
            if (name.isEmpty()) {
                edtName.setError("Vui lòng nhập họ tên");
                isValid = false;
            }
            if (email.isEmpty()) {
                edtEmail.setError("Vui lòng nhập email");
                isValid = false;
            }
            if (phone.isEmpty()) {
                edtPhone.setError("Vui lòng nhập số điện thoại");
                isValid = false;
            }
            if (address.isEmpty()) {
                edtAddress.setError("Vui lòng nhập địa chỉ");
                isValid = false;
            }
            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                isValid = false;
            }
            if (birthdate.isEmpty()) {
                edtBirthdate.setError("Vui lòng chọn ngày sinh");
                isValid = false;
            }
            if (!isValid) return;

            // Chuyển định dạng ngày cho API
            String birthdateForApi = "";
            try {
                String[] parts = birthdate.split("/");
                birthdateForApi = parts[2] + "-" + parts[1] + "-" + parts[0];
            } catch (Exception e) {
                birthdateForApi = birthdate;
            }

            authViewModel.register(name, phone, email, password, address, birthdateForApi);
        });
    }

    private void observeViewModel() {
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                btnRegister.setEnabled(false);
                btnRegister.setText("Đang xử lý...");
            } else {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng ký");
            }
        });

        authViewModel.getAuthResult().observe(this, authResponse -> {
            if (authResponse != null && authResponse.isSuccess()) {
                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendar.setTimeInMillis(selection);

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            String selectedDate = String.format(Locale.getDefault(),
                    "%02d/%02d/%d", day, month, year);
            edtBirthdate.setText(selectedDate);
            edtBirthdate.setError(null);
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }


}