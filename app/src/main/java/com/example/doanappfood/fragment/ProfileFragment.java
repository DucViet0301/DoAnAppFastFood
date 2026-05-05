package com.example.doanappfood.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.SlideEffect;
import com.example.doanappfood.activity.ChangePasswordActivity;
import com.example.doanappfood.activity.LoginActivity;
import com.example.doanappfood.activity.RegisterActivity;

public class ProfileFragment extends Fragment {

    private AppCompatButton btnLogin, btnRegister;
    private LinearLayout layoutNotLoggedIn, layoutLoggedIn, btnLogoutItem;
    private TextView tvUserNameHeader, tvFullNameProfile, tvPhoneProfile, tvEmailProfile, tvDobProfile;
    private TextView tvChangePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);
        checkLoginStatus();

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnRegister.setOnClickListener(v -> {
            SlideEffect.startActivity(requireActivity(), RegisterActivity.class);
        });

        btnLogoutItem.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản này không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Toast.makeText(requireActivity(), "Đã đăng xuất!", Toast.LENGTH_SHORT).show();

                        checkLoginStatus();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
        tvChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        return view;
    }

    public void initView(View view){
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);

        layoutNotLoggedIn = view.findViewById(R.id.layoutNotLoggedIn);
        layoutLoggedIn = view.findViewById(R.id.layoutLoggedIn);

        tvUserNameHeader = view.findViewById(R.id.tvUserNameHeader);
        tvFullNameProfile = view.findViewById(R.id.tvFullNameProfile);
        tvPhoneProfile = view.findViewById(R.id.tvPhoneProfile);
        tvEmailProfile = view.findViewById(R.id.tvEmailProfile);
        tvDobProfile = view.findViewById(R.id.tvDobProfile);

        btnLogoutItem = view.findViewById(R.id.btnLogoutItem);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Đã đăng nhập: Hiện cục Profile, Nút Đăng xuất, Nút Đổi mật khẩu
            layoutNotLoggedIn.setVisibility(View.GONE);
            layoutLoggedIn.setVisibility(View.VISIBLE);
            btnLogoutItem.setVisibility(View.VISIBLE);
            tvChangePassword.setVisibility(View.VISIBLE);

            String name = sharedPreferences.getString("userName", "Người dùng");
            String email = sharedPreferences.getString("userEmail", "Chưa cập nhật");
            String phone = sharedPreferences.getString("userPhone", "Chưa cập nhật");
            String dob = sharedPreferences.getString("userDob", "Chưa cập nhật");

            tvUserNameHeader.setText(name);
            tvFullNameProfile.setText("Họ tên: " + name);
            tvEmailProfile.setText("Email: " + email);
            tvPhoneProfile.setText("SĐT: " + phone);
            tvDobProfile.setText("Ngày sinh: " + dob);
        } else {
            // Chưa đăng nhập: Hiện cục Yêu cầu đăng nhập, ẨN CÁC NÚT KIA ĐI
            layoutNotLoggedIn.setVisibility(View.VISIBLE);
            layoutLoggedIn.setVisibility(View.GONE);
            btnLogoutItem.setVisibility(View.GONE);
            tvChangePassword.setVisibility(View.GONE);
        }
    }
}