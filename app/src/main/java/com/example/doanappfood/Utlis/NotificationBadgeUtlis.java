package com.example.doanappfood.Utlis;

import android.graphics.Color;

import com.example.doanappfood.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NotificationBadgeUtlis {

    public static void showNotificationBadge(BottomNavigationView bottomNav){
        BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.notification);
        badge.setVisible(true);
        badge.clearNumber();
        badge.setBackgroundColor(Color.RED);
    }
    public static void hideNotificationBadge(BottomNavigationView bottomNav) {
        bottomNav.removeBadge(R.id.notification);
    }


}
