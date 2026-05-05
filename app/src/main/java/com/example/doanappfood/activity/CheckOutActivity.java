package com.example.doanappfood.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.Switch;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.FusedLocationHelper;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.Utlis.LocationHelper;
import com.example.doanappfood.Utlis.LocationPermissionHelper;
import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.fragment.NotifactionFragment;
import com.example.doanappfood.fragment.StoreFragment;
import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.repository.OrderRepository;
import com.example.doanappfood.viewmodel.OrderViewModel;
import com.google.android.gms.internal.location.zzbb;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import vn.momo.momo_partner.AppMoMoLib;

public class CheckOutActivity extends AppCompatActivity {
    private ImageView btnBackCheckOut, imgMarker;
    private Button btnCheckOut;
    private EditText editAddress, edtNote;
    private TextView tvAddMore, tvSelectedPayment , tvTotalPriceCheckOut, tvOldPrice;
    CardView cardPaymentMethod;
    View  dimOverlay;
    OrderViewModel viewModel;
    private boolean isLocationFetched = false;
    private double finalTimeDelivery = 30;
    private FusedLocationHelper locationHelper;
    private LinearLayout layoutCOD, layoutMoMo;
    ConstraintLayout selectOptionPayment, footerCheckout;
    private LocationPermissionHelper permissionHelper;
    private boolean isAutoFillFromLocation = false;
    private String selectedPayment = "Tiền mặt (COD)";
    private Switch switchUtensils, switchKetchup, switchChili;
    private double saleTotal = 0;
    private double listTotal = 0;
    private int user_id;
    private String cartJson = "";
    private String distance = "";
    // Thanh Toán
    private String amount = "10000";
    private String fee = "0";
    int environment = 0;//developer default
    private String merchantName = "FastFoodFour";
    private String merchantCode = "MOMOC2IC20220510";
    private String merchantNameLabel = "FastFoodFour";
    private String description = "mua hàng online";
    private int ppthanhtoan = 0;
    private String tempOrderId = "";

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

        tvAddMore.setOnClickListener(v -> {
            Intent intent = new Intent(CheckOutActivity.this, MainActivity.class);

            intent.putExtra("open_tab", "store");
            intent.putExtra("IdCate", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });


        btnBackCheckOut.setOnClickListener(v -> finish());
//        btnCheckOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("CHECKOUT_INFO",
//                        "Địa chỉ: " + editAddress.getText().toString()
//                                + " | time_delivery = " + time_delivery + " phút");
////                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//            }
//        });
        imgMarker.setOnClickListener(v -> handleLocationClick());
        setupAddressWatcher();
        saleTotal = getIntent().getDoubleExtra("sale_total", 0);
        listTotal = getIntent().getDoubleExtra("list_total", 0);
        user_id = getIntent().getIntExtra("user_id",1);
        cartJson = getIntent().getStringExtra("cart_json");
        initControl();
        updateBottomTotal();
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT);
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
        tvTotalPriceCheckOut = findViewById(R.id.tvTotalPriceCheckOut);
        tvOldPrice = findViewById(R.id.tvOldPriceCheckout);
        switchUtensils = findViewById(R.id.switchUtensils);
        switchKetchup = findViewById(R.id.switchKetchup);
        switchChili = findViewById(R.id.switchChili);
        edtNote = findViewById(R.id.edtNote);
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);
    }

    public void setupClick() {
        cardPaymentMethod.setOnClickListener(v -> showPaymentOptions());
        layoutCOD.setOnClickListener(v -> {
            selectedPayment = "Tiền mặt (COD)";
            ppthanhtoan = 0;
            tvSelectedPayment.setText(selectedPayment);
            hidePaymentOptions();
        });

        layoutMoMo.setOnClickListener(v -> {
            selectedPayment = "Ví MoMo";
            ppthanhtoan = 1;
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

    private void setupAddressWatcher() {
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
                    finalTimeDelivery = 30;
                    isLocationFetched = false;
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
                isAutoFillFromLocation = true;
                editAddress.setText(address);
                isAutoFillFromLocation = false;
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
                        try {
                            String number = duration.replaceAll("[^0-9.]", "");
                            double routeMinute = Double.parseDouble(number);
                            finalTimeDelivery = routeMinute + 15;
                            isLocationFetched = true;
                            CheckOutActivity.this.distance = distance;
                        } catch (Exception e) {
                            finalTimeDelivery = 30;
                            isLocationFetched = false;
                        }
                        Log.d("CHECKOUT_INFO",
                                "Tên cửa hàng: " + storeName +
                                        " | Quãng đường: " + distance +
                                        " | Thời gian: " + duration +
                                        " | Thời gian giao hàng: " + finalTimeDelivery);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                finalTimeDelivery = 30;
                Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
    private void updateBottomTotal() {
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        tvTotalPriceCheckOut.setText(fmt.format(saleTotal) + " đ");

        if (listTotal > saleTotal) {
            tvOldPrice.setVisibility(View.VISIBLE);
            tvOldPrice.setText(fmt.format(listTotal) + " đ");
            tvOldPrice.setPaintFlags(
                    tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
        } else {
            tvOldPrice.setVisibility(View.GONE);
        }
    }
    // gọi api tạo đơn hàngI
    private void placeOrder(String paymentMethod) {
        Map<String, Object> checkoutData = new LinkedHashMap<>();
        checkoutData.put("user_id", user_id);
        checkoutData.put("address", editAddress.getText().toString());
        checkoutData.put("payment_method", paymentMethod);
        checkoutData.put("time_delivery", finalTimeDelivery);
        checkoutData.put("sale_total", saleTotal);
        checkoutData.put("note", edtNote.getText().toString());
        checkoutData.put("distance", distance);
        checkoutData.put("utensils", switchUtensils.isChecked());
        checkoutData.put("ketchup", switchKetchup.isChecked());
        checkoutData.put("chili", switchChili.isChecked());
        checkoutData.put("cart_items", new Gson().fromJson(cartJson, List.class));

        String json = new Gson().toJson(checkoutData);
        viewModel.CheckOut(json);
    }

    private void initControl() {
        viewModel.init();
        viewModel.getMessModelMutableLiveData().observe(this, new Observer<MessModel>() {
            @Override
            public void onChanged(MessModel messModel) {
                if (messModel.isSuccess()) {
                    Toast.makeText(getApplicationContext(), messModel.getMessage(), Toast.LENGTH_LONG).show();
                    CartDAO cartDAO = new CartDAO(CheckOutActivity.this);
                    cartDAO.clearCart();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("SELECTED_ID", R.id.notification);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
        btnCheckOut.setOnClickListener(v -> {
            if (editAddress.getText().toString().isEmpty()){
                Toast.makeText(this, "Vui lòng nhập địa chỉ nhận hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if(ppthanhtoan == 0){
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Xác nhận thanh toán")
                        .setMessage("Bạn có chắc muốn đặt hàng với phương thức thanh toán \""
                                + selectedPayment + "\" không?")
                        .setPositiveButton("Xác nhận", (dialog, which) -> {
                            placeOrder(selectedPayment);
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
            else if(ppthanhtoan == 1){
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Xác nhận thanh toán")
                        .setMessage("Bạn có chắc muốn đặt hàng với phương thức thanh toán \""
                                + selectedPayment + "\" không?")
                        .setPositiveButton("Xác nhận", (dialog, which) -> {
                            requestPayment();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
            else {
                return;
            }


        });
    }
    //Get token through MoMo app
    private void requestPayment() {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        if (tvTotalPriceCheckOut.getText().toString() != null && tvTotalPriceCheckOut.getText().toString().trim().length() != 0)
            amount = tvTotalPriceCheckOut.getText().toString().trim();
        tempOrderId = "ORDER_" + System.currentTimeMillis();

        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        eventValue.put("orderId", tempOrderId); //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderLabel", tempOrderId); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", "0"); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId",  merchantCode+"merchant_billId_"+System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(this, eventValue);


    }
    //Get token callback from MoMo app an submit to server side
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if(data != null) {
                if(data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("thanhcong", data.getStringExtra("message"));
//                    tvMessage.setText("message: " + "Get token " + data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if(env == null){
                        env = "app";
                    }

                    if(token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                        placeOrder(selectedPayment);
                    } else {
                        Log.d("thanhcong", "khong thanh cong");
//                        tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                    }
                } else if(data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null?data.getStringExtra("message"):"Thất bại";
                    Log.d("thanhcong", "khong thanh cong");
//                    tvMessage.setText("message: " + message);
                } else if(data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("thanhcong", "khong thanh cong");
//                    tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                } else {
                    //TOKEN FAIL
                    Log.d("thanhcong", "khong thanh cong");
//                    tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
                }
            } else {
                Log.d("thanhcong", "khong thanh cong");
//                tvMessage.setText("message: " + this.getString(R.string.not_receive_info));
            }
        } else {
            Log.d("thanhcong", "khong thanh cong");
//            tvMessage.setText("message: " + this.getString(R.string.not_receive_info_err));
        }
    }
}
