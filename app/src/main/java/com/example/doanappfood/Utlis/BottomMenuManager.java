package com.example.doanappfood.Utlis;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.doanappfood.R;
import com.example.doanappfood.activity.MainActivity;
import com.example.doanappfood.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomMenuManager {

    private final MainActivity activity;
    private final BottomNavigationView bottomNav;
    private final FloatingActionButton fab;
    private final ActivityMainBinding binding;
    private FloatingActionButton  fab_chatbox;
    private ImageView shoppingcart;
    private ConstraintLayout headerLayout;
    private TextView badge_count;

    public BottomMenuManager(MainActivity activity, ActivityMainBinding binding,
                             BottomNavigationView bottomNav, FloatingActionButton fab,
                             FloatingActionButton fab_chatbox) {
        this.activity = activity;
        this.binding = binding;
        this.bottomNav = bottomNav;
        this.fab = fab;
        setup();
        updateCartBadge();
    }

    public void updateCartBadge() {
        if (badge_count == null) return;

        com.example.doanappfood.data.CartDAO cartDAO = new com.example.doanappfood.data.CartDAO(activity);
        com.example.doanappfood.Utlis.SessionManager sessionManager = new com.example.doanappfood.Utlis.SessionManager(activity);

        int count = 0;
        if (sessionManager.isLoggedIn()) {
            count = cartDAO.getCount(sessionManager.getUserId());
        }

        if (count > 0) {
            badge_count.setVisibility(View.VISIBLE);
            badge_count.setText(String.valueOf(count));
        } else {
            badge_count.setVisibility(View.GONE);
        }
    }

    private void setup() {
        shoppingcart = binding.layoutHeader.icShoppingcart;
        headerLayout = binding.layoutHeader.headerLayout;
        badge_count = binding.layoutHeader.badgeCount;
        fab_chatbox = binding.fabChatbox;

        fab.setOnClickListener(v -> bottomNav.setSelectedItemId(R.id.store));
        setHomeColors();

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            fab.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            shoppingcart.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            headerLayout.setBackgroundResource(R.color.do_tuoi);
            badge_count.setBackgroundResource(R.drawable.circle_border);
            badge_count.setTextColor(ContextCompat.getColor(activity, R.color.do_nhat));
            updateCartBadge();

            if (itemId == R.id.home) {
                setHomeColors();
            }
            activity.switchToNavId(itemId);
            return true;
        });
    }
    private void setHomeColors() {
        shoppingcart.setImageTintList(ColorStateList.valueOf(Color.BLACK));
        headerLayout.setBackgroundResource(R.color.xanh_ngoc);
        badge_count.setBackgroundResource(R.drawable.circle_border_v1);
        badge_count.setTextColor(ContextCompat.getColor(activity, R.color.white));
    }
}