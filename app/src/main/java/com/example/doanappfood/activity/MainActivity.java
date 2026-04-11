package com.example.doanappfood.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.BottomMenuManager;
import com.example.doanappfood.databinding.ActivityMainBinding;
import com.example.doanappfood.fragment.HistoryFragment;
import com.example.doanappfood.fragment.HomeFragment;
import com.example.doanappfood.fragment.NotifactionFragment;
import com.example.doanappfood.fragment.ProfileFragment;
import com.example.doanappfood.fragment.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    FloatingActionButton fab;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        bottomNav = findViewById(R.id.bottomNavigationView);
        fab = findViewById(R.id.fab);
        new BottomMenuManager(this, bottomNav, fab);
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) replaceFragment(new HomeFragment());
            else if (itemId == R.id.history) replaceFragment(new HistoryFragment());
            else if (itemId == R.id.store) replaceFragment(new StoreFragment());
            else if (itemId == R.id.notification) replaceFragment(new NotifactionFragment());
            else if (itemId == R.id.profile) replaceFragment(new ProfileFragment());
            return true;
        });

        // Bấm vào Giỏ hàng
        ImageView btnCart = findViewById(R.id.ic_shoppingcart);
        TextView tvQuantity = findViewById(R.id.tvquantity);
        View.OnClickListener openCartListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ĐÃ SỬA: Đổi lệnh mở Fragment thành lệnh mở Activity (Dùng Intent)
                android.content.Intent intent = new android.content.Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        };

        if (btnCart != null) btnCart.setOnClickListener(openCartListener);
        if (tvQuantity != null) tvQuantity.setOnClickListener(openCartListener);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            bottomNav.setSelectedItemId(R.id.home);
        }

        // ====================================================================
        // ĐOẠN PHÉP THUẬT: ẨN MENU + ẨN HEADER + CO DÃN KHOẢNG TRẮNG
        // ====================================================================
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                View fragmentContainer = findViewById(R.id.fragment_container);
                View headerLayout = findViewById(R.id.layout_header); // Lấy thanh header Lotteria

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    // Ẩn Menu và Ẩn Header
                    findViewById(R.id.bottomAppBar).setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    if (headerLayout != null) headerLayout.setVisibility(View.GONE);

                    if (fragmentContainer != null) fragmentContainer.setPadding(0, 0, 0, 0);
                } else {
                    // Hiện lại Menu và Hiện lại Header
                    findViewById(R.id.bottomAppBar).setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);
                    if (headerLayout != null) headerLayout.setVisibility(View.VISIBLE);

                    if (fragmentContainer != null) {
                        int padding80dp = (int) (80 * getResources().getDisplayMetrics().density);
                        fragmentContainer.setPadding(0, 0, 0, padding80dp);
                    }
                }
            }
        });
    }

    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
}