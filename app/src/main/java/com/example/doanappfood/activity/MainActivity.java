package com.example.doanappfood.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
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
//import com.example.doanappfood.fragment.MapFragment;
import com.example.doanappfood.fragment.MapFragment;
import com.example.doanappfood.fragment.NotifactionFragment;
import com.example.doanappfood.fragment.ProfileFragment;
import com.example.doanappfood.fragment.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fab;
    private  int currentId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initViews();

        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }

    private void initViews() {
        bottomNav = binding.bottomNavigationView;
        fab = binding.fab;

        new BottomMenuManager(this, bottomNav, fab);
        bottomNav.setBackground(null);
    }



    private void handleIntent(Intent intent) {
        int targetId = R.id.home; // Mặc định vào Home
        if (intent != null && intent.hasExtra("SELECTED_ID")) {
            targetId = intent.getIntExtra("SELECTED_ID", R.id.home);
        }
        bottomNav.setSelectedItemId(targetId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    public void replaceFragment(Fragment fragment, int nextId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isLeftToRight(currentId, nextId)){
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else{
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        currentId = nextId;
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    private  boolean isLeftToRight(int current, int next){
        int currentPosition = getPosition(current);
        int nextPosition = getPosition(next);
        return nextPosition > currentPosition;
    }
    private int getPosition(int id){
        if(id == R.id.home) return 1;
        if(id == R.id.history) return 2;
        if(id == R.id.store) return 3;
        if(id == R.id.notification) return 4;
        if(id == R.id.profile) return 5;
        if (id == R.id.maps) return 6;
        return 0;
    }
}