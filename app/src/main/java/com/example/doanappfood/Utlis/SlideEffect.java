package com.example.doanappfood.Utlis;

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
}
//  Fragemtn -> requireActivity() ; Activity -> MainActivity.this
