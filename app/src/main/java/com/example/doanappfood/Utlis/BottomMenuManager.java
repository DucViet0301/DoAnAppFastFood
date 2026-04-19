package com.example.doanappfood.Utlis;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.doanappfood.R;
import com.example.doanappfood.activity.MainActivity;
import com.example.doanappfood.databinding.ActivityMainBinding;
import com.example.doanappfood.fragment.HistoryFragment;
import com.example.doanappfood.fragment.HomeFragment;
import com.example.doanappfood.fragment.NotifactionFragment;
import com.example.doanappfood.fragment.ProfileFragment;
import com.example.doanappfood.fragment.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomMenuManager {

    private final MainActivity activity;
    private FloatingActionButton fab_chatbox;
    private final BottomNavigationView bottomNav;
    private final FloatingActionButton fab;
    private final ActivityMainBinding binding;
    private ImageView shoppingcart;
    private ConstraintLayout headerLayout;
    private  TextView badge_count;

    public BottomMenuManager(MainActivity activity, ActivityMainBinding binding, BottomNavigationView bottomNav, FloatingActionButton fab, FloatingActionButton fab_chatbox) {
        this.activity = activity;
        this.binding = binding;
        this.bottomNav = bottomNav;
        this.fab = fab;
        this.fab_chatbox = fab_chatbox;
        setup();
    }

    private void setup() {
        shoppingcart = binding.layoutHeader.icShoppingcart;
        headerLayout = binding.layoutHeader.headerLayout;
        badge_count = binding.layoutHeader.badgeCount;


        fab.setOnClickListener(v -> {
            bottomNav.setSelectedItemId(R.id.store);
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            fab.setImageTintList(ColorStateList.valueOf(Color.WHITE));

            shoppingcart.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            headerLayout.setBackgroundResource(R.color.do_tuoi);
            badge_count.setBackgroundResource(R.drawable.circle_border);
            badge_count.setTextColor(ContextCompat.getColor(activity, R.color.do_nhat));

            Fragment selectedFragment = null;

            if (itemId == R.id.home) {
                shoppingcart.setImageTintList(ColorStateList.valueOf(Color.BLACK));
                headerLayout.setBackgroundResource(R.color.xanh_ngoc);
                badge_count.setBackgroundResource(R.drawable.circle_border_v1);
                badge_count.setTextColor(ContextCompat.getColor(activity, R.color.white));
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.history) {
                selectedFragment = new HistoryFragment();
            } else if (itemId == R.id.store) {
                selectedFragment = new StoreFragment();
            } else if (itemId == R.id.notification) {
                selectedFragment = new NotifactionFragment();
            } else if (itemId == R.id.profile) {
                selectedFragment = new ProfileFragment();
            }
            if(selectedFragment != null){
                activity.replaceFragment(selectedFragment, itemId);
                return true;
            }
            return true;
        });
    }
}