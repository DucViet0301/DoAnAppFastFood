package com.example.doanappfood.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.FusedLocationHelper;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.Utlis.LocationHelper;
import com.example.doanappfood.Utlis.LocationPermissionHelper;
import com.example.doanappfood.fragment.StoreFragment;
import com.google.android.gms.internal.location.zzbb;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CheckOutActivity extends AppCompatActivity {
    private ImageView btnBackCheckOut, imgMarker;
    private Button btnCheckOut;
    private EditText editAddress;
    private TextView tvAddMore, tvSelectedPayment;
    CardView cardPaymentMethod;
    View  dimOverlay;
    private double time_delivery = 30;
    private FusedLocationHelper locationHelper;
    private  LinearLayout layoutCOD, layoutMoMo;
    ConstraintLayout selectOptionPayment, footerCheckout;
    private LocationPermissionHelper permissionHelper;
    private  boolean isAutoFillFromLocation = false;
    private String selectedPayment = "Tiền mặt (COD)";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_out);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        intitViews();
        setupClick();
        permissionHelper = new LocationPermissionHelper(this);
        locationHelper = new FusedLocationHelper(this);

        tvAddMore.setOnClickListener( v -> {
            Intent intent = new Intent(CheckOutActivity.this, MainActivity.class);

            intent.putExtra("open_tab", "store");
            intent.putExtra("IdCate", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


        btnBackCheckOut.setOnClickListener(v -> finish());
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CHECKOUT_INFO",
                        "Địa chỉ: " + editAddress.getText().toString()
                                + " | time_delivery = " + time_delivery + " phút");
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        imgMarker.setOnClickListener(v -> handleLocationClick());
        setupAddressWatcher();
    }

    public void intitViews() {
        btnBackCheckOut = findViewById(R.id.btnBackCheckOut);
        btnCheckOut = findViewById(R.id.btnPlaceOrder);
        imgMarker = findViewById(R.id.imgMarker);
        editAddress = findViewById(R.id.editAddress);
        tvAddMore = findViewById(R.id.tvAddMore);
        tvSelectedPayment = findViewById(R.id.tvSelectedPayment);
        cardPaymentMethod = findViewById(R.id.cardPaymentMethod);
        selectOptionPayment = findViewById(R.id.selectOptionPayment);
        footerCheckout = findViewById(R.id.footerCheckout);
        dimOverlay = findViewById(R.id.dimOverlay);
        layoutCOD = findViewById(R.id.layoutCOD);
        layoutMoMo = findViewById(R.id.layoutMoMo);

    }
    public void setupClick() {
        cardPaymentMethod.setOnClickListener(v -> showPaymentOptions());
        layoutCOD.setOnClickListener(v -> {
            selectedPayment = "Tiền mặt (COD)";
            tvSelectedPayment.setText(selectedPayment);
            hidePaymentOptions();
        });

        layoutMoMo.setOnClickListener(v -> {
            selectedPayment = "Ví MoMo";
            tvSelectedPayment.setText(selectedPayment);
            hidePaymentOptions();
        });

        dimOverlay.setOnClickListener(v -> hidePaymentOptions());
    }
    private void showPaymentOptions() {
        footerCheckout.setVisibility(View.GONE);
        dimOverlay.setVisibility(View.VISIBLE);
        selectOptionPayment.setVisibility(View.VISIBLE);

        selectOptionPayment.post(() -> {
            float height = selectOptionPayment.getHeight();
            selectOptionPayment.setTranslationY(height);
            selectOptionPayment.animate()
                    .translationY(0)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        });

        dimOverlay.setAlpha(0f);
        dimOverlay.animate().alpha(1f).setDuration(300).start();
    }

    private void hidePaymentOptions() {
        footerCheckout.setVisibility(View.VISIBLE);
        selectOptionPayment.animate()
                .translationY(selectOptionPayment.getHeight() + 100)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> {
                    selectOptionPayment.setVisibility(View.GONE);
                })
                .start();

        dimOverlay.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> dimOverlay.setVisibility(View.GONE))
                .start();
    }

    private  void setupAddressWatcher(){
        editAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!isAutoFillFromLocation){
                    time_delivery = 30;
                }
            }
        });
    }

    private void handleLocationClick() {
        if (permissionHelper.hasPermission()) {
            fetchLocationAndStor();
        } else {
            permissionHelper.requestPermission();
        }
    }

    private void fetchLocationAndStor() {
        locationHelper.fetchAddress(new FusedLocationHelper.OnAddressCallback() {
            @Override
            public void onSuccess(String address) {
                editAddress.setText(address);
                Toast.makeText(CheckOutActivity.this, "Đã cập nhật vị trí!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        locationHelper.fetchLocation(new FusedLocationHelper.OnLocationCallback() {
            @Override
            public void onSuccess(double lat, double lng) {
                locationHelper.getNearStore(lat, lng, new FusedLocationHelper.OnRouteCallback() {
                    @Override
                    public void onSuccess(String storeName, String distance, String duration) {
                        try{
                            String number = duration.replaceAll("[^0-9.]", "");
                            double routeMinute = Double.parseDouble(number);
                            time_delivery = routeMinute + 15;
                        } catch (Exception e) {
                            time_delivery = 30;
                        }
                        Log.d("CHECKOUT_INFO",
                                "Tên cửa hàng: " + storeName +
                                        " | Quãng đường: " + distance +
                                        " | Thời gian: " + duration +
                                        " | Thời gian giao hàng: " + time_delivery);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                time_delivery = 30;
                Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}
