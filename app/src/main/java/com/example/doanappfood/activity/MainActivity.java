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
import com.example.doanappfood.fragment.HistoryFragment;
import com.example.doanappfood.fragment.HomeFragment;
import com.example.doanappfood.fragment.NotifactionFragment;
import com.example.doanappfood.fragment.ProfileFragment;
import com.example.doanappfood.fragment.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int CURRENT_USER_ID = 1;
    private BottomNavigationView bottomNav;
    private FloatingActionButton fab, fab_chatbox;
    private TextView badgecount;
    private ImageView btnShoppingacart;

    private HomeFragment homeFragment;
    private StoreFragment storeFragment;
    private HistoryFragment historyFragment;
    private NotifactionFragment notifactionFragment;
    private ProfileFragment profileFragment;
    private Fragment activeFragment;

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
        setupFragments();
        initViews();
        setupClick();

        if (savedInstanceState == null) {
            handleIntent(getIntent());
        }

        fab_chatbox.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatBotActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    public Fragment getActiveFragment() {
        return activeFragment;
    }

    public void setActiveFragment(Fragment fragment) {
        activeFragment = fragment;
    }
    private void setupFragments() {
        homeFragment = new HomeFragment();
        storeFragment = new StoreFragment();
        historyFragment = new HistoryFragment();
        notifactionFragment = new NotifactionFragment();
        profileFragment = new ProfileFragment();
        activeFragment = homeFragment;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, profileFragment, "profile").hide(profileFragment)
                .add(R.id.fragment_container, notifactionFragment, "notification").hide(notifactionFragment)
                .add(R.id.fragment_container, historyFragment, "history").hide(historyFragment)
                .add(R.id.fragment_container, storeFragment, "store").hide(storeFragment)
                .add(R.id.fragment_container, homeFragment, "home")
                .commit();
    }

    private void initViews() {
        badgecount = binding.layoutHeader.badgeCount;
        bottomNav = binding.bottomNavigationView;
        btnShoppingacart = binding.layoutHeader.icShoppingcart;
        fab_chatbox = binding.fabChatbox;
        fab = binding.fab;
        new BottomMenuManager(this, binding, bottomNav, fab, fab_chatbox);
        bottomNav.setBackground(null);
        startFabAnimation();
    }

    public void setupClick() {
        btnShoppingacart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
    private void handleIntent(Intent intent) {
        if (intent == null) {
            bottomNav.setSelectedItemId(R.id.home);
            return;
        }
        String openTab = intent.getStringExtra("open_tab");
        if ("store".equals(openTab)) {
            int cateId = intent.getIntExtra("IdCate", 1);
            openStoreTab(cateId);
            return;
        }
        int targetId = intent.getIntExtra("SELECTED_ID", R.id.home);
        bottomNav.setSelectedItemId(targetId);
    }

    private void openStoreTab(int cateId) {
        Bundle bundle = new Bundle();
        bundle.putInt("IdCate", cateId);
        storeFragment.setArguments(bundle);
        switchTab(storeFragment, R.id.store);
        bottomNav.setSelectedItemId(R.id.store);
    }
    public void openStoreWithCategory(int categoryId) {
        if (activeFragment == storeFragment) {
            storeFragment.loadCategoryFromHome(categoryId);
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("IdCate", categoryId);
            storeFragment.setArguments(bundle);
            bottomNav.setSelectedItemId(R.id.store);
        }
    }
    public void switchTab(Fragment target, int navId) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .hide(activeFragment)
                .show(target)
                .commit();
        activeFragment = target;
    }
    public void switchToNavId(int navId) {
        Fragment target;
        if (navId == R.id.home)         target = homeFragment;
        else if (navId == R.id.store)   target = storeFragment;
        else if (navId == R.id.history) target = historyFragment;
        else if (navId == R.id.notification) target = notifactionFragment;
        else if (navId == R.id.profile) target = profileFragment;
        else target = homeFragment;
        switchTab(target, navId);
    }
    public void replaceFragment(Fragment fragment, int nextId) {
        switchToNavId(nextId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBadge();
    }

    private void updateBadge() {
        CartDAO cartDAO = new CartDAO(this);
        int count = cartDAO.getCount(CURRENT_USER_ID);
        if (count > 0) {
            badgecount.setVisibility(View.VISIBLE);
            badgecount.setText(String.valueOf(count));
        } else {
            badgecount.setVisibility(View.GONE);
        }
    }

    public void startFabAnimation() {
        android.view.animation.RotateAnimation rotate = new android.view.animation.RotateAnimation(
                0, 360,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
                android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(3000);
        rotate.setRepeatCount(android.view.animation.Animation.INFINITE);
        rotate.setInterpolator(new android.view.animation.LinearInterpolator());
        fab_chatbox.startAnimation(rotate);
    }
}