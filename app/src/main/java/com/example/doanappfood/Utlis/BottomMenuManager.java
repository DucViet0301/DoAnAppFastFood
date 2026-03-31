package com.example.doanappfood.Utlis;

import android.content.res.ColorStateList;
import android.graphics.Color;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.doanappfood.R;
import com.example.doanappfood.activity.MainActivity;
import com.example.doanappfood.fragment.HistoryFragment;
import com.example.doanappfood.fragment.HomeFragment;
import com.example.doanappfood.fragment.NotifactionFragment;
import com.example.doanappfood.fragment.ProfileFragment;
import com.example.doanappfood.fragment.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomMenuManager {

    private final MainActivity activity; // Đổi thành MainActivity để gọi được hàm replaceFragment
    private final BottomNavigationView bottomNav;
    private final FloatingActionButton fab;

    public BottomMenuManager(MainActivity activity, BottomNavigationView bottomNav, FloatingActionButton fab) {
        this.activity = activity;
        this.bottomNav = bottomNav;
        this.fab = fab;
        setup();
    }

    private void setup() {
        fab.setOnClickListener(v -> {
            bottomNav.setSelectedItemId(R.id.store);
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            fab.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            Fragment selectedFragment = null;

            if (itemId == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.store) {
                fab.setImageTintList(ColorStateList.valueOf(Color.RED));
                selectedFragment = new StoreFragment();
            } else if (itemId == R.id.notification) {
                selectedFragment = new NotifactionFragment();
            } else if (itemId == R.id.profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                activity.replaceFragment(selectedFragment);
                return true;
            }

            return false;
        });
    }
}