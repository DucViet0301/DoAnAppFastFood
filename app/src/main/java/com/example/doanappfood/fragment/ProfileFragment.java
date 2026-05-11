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
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.Utlis.SlideEffect;
import com.example.doanappfood.activity.ChangePasswordActivity;
import com.example.doanappfood.activity.LoginActivity;
import com.example.doanappfood.activity.RegisterActivity;
import com.example.doanappfood.model.AuthModel;
import com.example.doanappfood.repository.AuthRepository;
import com.example.doanappfood.data.CartDAO;

public class ProfileFragment extends Fragment {

    private AppCompatButton btnLogin, btnRegister;
    private LinearLayout layoutNotLoggedIn, layoutLoggedIn, btnLogoutItem;
    private TextView tvUserNameHeader, tvFullName, tvBirthDate, tvPhone, tvEmail, tvChangePassword;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        initView(view);
        updateUI();

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnRegister.setOnClickListener(v -> SlideEffect.startActivity(requireActivity(), RegisterActivity.class));

        tvChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogoutItem.setOnClickListener(v -> {
            AuthRepository authRepository = new AuthRepository(requireContext());
            authRepository.logout(new AuthRepository.AuthCallback() {
                @Override
                public void onSuccess(AuthModel response) {
                    performLogout();
                }

                @Override
                public void onFailure(String errorMessage) {
                    performLogout();
                }
            });
        });

        return view;
    }

    private void performLogout() {
        // Không xóa giỏ hàng ở đây để khi đăng nhập lại vẫn còn dữ liệu
        sessionManager.logout();

        if (requireActivity() instanceof com.example.doanappfood.activity.MainActivity) {
            ((com.example.doanappfood.activity.MainActivity) requireActivity()).updateBadge();
        }

        Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        updateUI();
    }

    private void initView(View view) {
        btnLogin = view.findViewById(R.id.btnLogin);
        btnRegister = view.findViewById(R.id.btnRegister);
        layoutNotLoggedIn = view.findViewById(R.id.layoutNotLoggedIn);
        layoutLoggedIn = view.findViewById(R.id.layoutLoggedIn);
        btnLogoutItem = view.findViewById(R.id.btnLogoutItem);

        tvUserNameHeader = view.findViewById(R.id.tvUserNameHeader);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvBirthDate = view.findViewById(R.id.tvBirthDate);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);
    }

    private void updateUI() {
        if (sessionManager.isLoggedIn()) {
            layoutNotLoggedIn.setVisibility(View.GONE);
            layoutLoggedIn.setVisibility(View.VISIBLE);
            btnLogoutItem.setVisibility(View.VISIBLE);

            tvUserNameHeader.setText(sessionManager.getUserName());
            tvFullName.setText("Họ tên: " + sessionManager.getUserName());

            String rawBirthDate = sessionManager.getUserBirthDate();
            String formattedDate = rawBirthDate;
            if (rawBirthDate != null && !rawBirthDate.isEmpty()) {
                String dateOnly = rawBirthDate;
                if (rawBirthDate.contains("T")) {
                    dateOnly = rawBirthDate.split("T")[0];
                } else if (rawBirthDate.contains(" ")) {
                    dateOnly = rawBirthDate.split(" ")[0];
                }
                if (dateOnly.contains("-")) {
                    String[] parts = dateOnly.split("-");
                    if (parts.length == 3) {
                        formattedDate = parts[2] + "/" + parts[1] + "/" + parts[0];
                    }
                }
            }

            tvBirthDate.setText("Ngày sinh: " + formattedDate);
            tvPhone.setText("SĐT: " + sessionManager.getUserPhone());
            tvEmail.setText("Email: " + sessionManager.getUserEmail());
        } else {
            layoutNotLoggedIn.setVisibility(View.VISIBLE);
            layoutLoggedIn.setVisibility(View.GONE);
            btnLogoutItem.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
