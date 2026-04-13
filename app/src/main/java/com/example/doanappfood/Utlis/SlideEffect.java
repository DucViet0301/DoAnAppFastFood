package com.example.doanappfood.Utlis;

import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.example.doanappfood.R;

public class SlideEffect {
    public  static  void changeFragment(FragmentActivity activity, Fragment fragment){
        activity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                )
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    public  static  void startActivity(FragmentActivity activity, Class<?> cls){
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);

        activity.overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
        );
    }
}
//  Fragemtn -> requireActivity() ; Activity -> MainActivity.this
