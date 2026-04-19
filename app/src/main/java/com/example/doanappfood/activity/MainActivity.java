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
import com.example.doanappfood.fragment.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int CURRENT_USER_ID = 1;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fab, fab_chatbox;
    private  int currentId = -1;
    private  TextView badgecount;
    private ImageView btnShoppingacart;


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
        setupClick();
        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }
    }
    private void initViews() {
        badgecount = binding.layoutHeader.badgeCount;
        bottomNav = binding.bottomNavigationView;
        btnShoppingacart = binding.layoutHeader.icShoppingcart;
        fab_chatbox = binding.fabChatbox;
        fab = binding.fab;

        new BottomMenuManager(this,binding,  bottomNav, fab, fab_chatbox);
        bottomNav.setBackground(null);
        startFabAnimation();
    }
    public  void setupClick(){
        btnShoppingacart.setOnClickListener( v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        });
    }
    private void handleIntent(Intent intent) {
        if(intent == null){
            bottomNav.setSelectedItemId(R.id.home);
        }
        String openTab =
                intent.getStringExtra("open_tab");
        if ("store".equals(openTab)) {
            int cateId = intent.getIntExtra("IdCate", 1);
            openStoreTab(cateId);
            return;
        }

        int targetId = R.id.home;
        if (intent != null && intent.hasExtra("SELECTED_ID")) {
            targetId = intent.getIntExtra("SELECTED_ID", R.id.home);
        }
        bottomNav.setSelectedItemId(targetId);
    }
    private void openStoreTab(int cateId) {

        bottomNav.setSelectedItemId(R.id.store);

        Bundle bundle = new Bundle();
        bundle.putInt("IdCate", cateId);

        Fragment fragment = new StoreFragment();
        fragment.setArguments(bundle);

        replaceFragment(fragment, R.id.store);
    }
    public  void startFabAnimation(){
        android.view.animation.RotateAnimation rotate = new android.view.animation.RotateAnimation(
                0, 360,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(3000);
        rotate.setRepeatCount(android.view.animation.Animation.INFINITE);
        rotate.setInterpolator(new android.view.animation.LinearInterpolator());
        fab_chatbox.startAnimation(rotate);
        fab_chatbox.setOnClickListener(v -> {
            // Intent intent = new Intent(MainActivity.this, ChatAiActivity.class);
            // startActivity(intent);
        });
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
    @Override
    protected  void onResume() {
        super.onResume();
        updateBadge();
    }

    private void updateBadge() {
        CartDAO cartDAO = new CartDAO(this);
        int count = cartDAO.getCount(CURRENT_USER_ID);
        if( count > 0){
            badgecount.setVisibility(View.VISIBLE);
            badgecount.setText(String.valueOf(count));
        }
        else{
            badgecount.setVisibility(View.GONE);
        }
    }
}