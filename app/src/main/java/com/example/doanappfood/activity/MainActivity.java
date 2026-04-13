package com.example.doanappfood.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.BottomMenuManager;
import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fab;
    private  int currentId = -1;
    private  TextView badgecount;
    private ImageView btnShoppingacrat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CartDAO cartDAO = new CartDAO(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initViews();
        btnShoppingacrat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CartActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        int count = cartDAO.getCount();
        if( count > 0){
            badgecount.setVisibility(View.VISIBLE);
            badgecount.setText(String.valueOf(count));
        }
        else{
            badgecount.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }


    private void initViews() {
        badgecount = binding.layoutHeader.badgeCount;
        bottomNav = binding.bottomNavigationView;
        btnShoppingacrat = binding.layoutHeader.icShoppingcart;

        fab = binding.fab;

        new BottomMenuManager(this,binding,  bottomNav, fab);
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
        int currentPos = getPosition(currentId);
        int nextPos = getPosition(nextId);

        if (currentPos != nextPos) {
            if (nextPos > currentPos) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            } else {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }

        currentId = nextId;
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    private int getPosition(int id){
        if(id == R.id.home || id == R.id.maps) return 1;
        if(id == R.id.history) return 2;
        if(id == R.id.store) return 3;
        if(id == R.id.notification) return 4;
        if(id == R.id.profile) return 5;
        return 0;
    }
}