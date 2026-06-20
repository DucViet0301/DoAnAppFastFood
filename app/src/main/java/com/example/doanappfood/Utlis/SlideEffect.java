package com.example.doanappfood.Utlis;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.example.doanappfood.R;
import com.example.doanappfood.activity.MainActivity;

public class SlideEffect {
    public static void changeFragment(FragmentActivity activity, Fragment targetFragment) {
        if (!(activity instanceof MainActivity)) return;
        MainActivity main = (MainActivity) activity;
        Fragment currentFragment = main.getActiveFragment();
        if (currentFragment == targetFragment) return;

        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out
                )
                .hide(currentFragment)
                .show(targetFragment)
                .commit();

        main.setActiveFragment(targetFragment);
    }

    public static void startActivity(FragmentActivity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
