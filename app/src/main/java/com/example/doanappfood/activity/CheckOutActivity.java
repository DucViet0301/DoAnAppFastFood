package com.example.doanappfood.activity;

import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.FusedLocationHelper;
import com.example.doanappfood.Utlis.Keyboard;
import com.example.doanappfood.Utlis.LocationPermissionHelper;
import com.example.doanappfood.Utlis.NotificationManager;
import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.model.NotificationModel;
import com.example.doanappfood.viewmodel.CheckOutViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckOutActivity extends AppCompatActivity {
    private ImageView btnBackCheckOut, imgMarker;
    private Button btnCheckOut;
    private EditText editAddress, edtNote;
    private TextView tvAddMore, tvSelectedPayment, tvTotalPriceCheckOut, tvOldPrice;
    private CardView cardPaymentMethod;
    private View dimOverlay;
    private LinearLayout layoutCOD, layoutMoMo;
    private ConstraintLayout selectOptionPayment, footerCheckout;
    private Switch switchUtensils, switchKetchup, switchChili;

    private CheckOutViewModel viewModel;
    private LocationPermissionHelper permissionHelper;
    private FusedLocationHelper locationHelper;

    private String selectedPayment = "Tiền mặt (COD)";
    private int ppthanhtoan = 0;
    private boolean isAutoFillFromLocation = false;

    private static final String MOMO_REDIRECT_HOST = "payment.doanappfood.vn";
    private static final String MOMO_REDIRECT_PATH = "/result";
    private static final int    REQUEST_MOMO_PAY   = 2001;

    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

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

        initViews();
        initHelpers();
        setupViewModel();
        setupClickListeners();
        setupAddressWatcher();
        updateBottomTotal();
    }


    private void initViews() {
        btnBackCheckOut      = findViewById(R.id.btnBackCheckOut);
        btnCheckOut          = findViewById(R.id.btnPlaceOrder);
        imgMarker            = findViewById(R.id.imgMarker);
        editAddress          = findViewById(R.id.editAddress);
        tvAddMore            = findViewById(R.id.tvAddMore);
        tvSelectedPayment    = findViewById(R.id.tvSelectedPayment);
        cardPaymentMethod    = findViewById(R.id.cardPaymentMethod);
        selectOptionPayment  = findViewById(R.id.selectOptionPayment);
        footerCheckout       = findViewById(R.id.footerCheckout);
        dimOverlay           = findViewById(R.id.dimOverlay);
        layoutCOD            = findViewById(R.id.layoutCOD);
        layoutMoMo           = findViewById(R.id.layoutMoMo);
        tvTotalPriceCheckOut = findViewById(R.id.tvTotalPriceCheckOut);
        tvOldPrice           = findViewById(R.id.tvOldPriceCheckout);
        switchUtensils       = findViewById(R.id.switchUtensils);
        switchKetchup        = findViewById(R.id.switchKetchup);
        switchChili          = findViewById(R.id.switchChili);
        edtNote              = findViewById(R.id.edtNote);
    }

    private void initHelpers() {
        permissionHelper = new LocationPermissionHelper(this);
        locationHelper   = new FusedLocationHelper(this);
    }


    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CheckOutViewModel.class);

        viewModel.init(
                getIntent().getDoubleExtra("sale_total", 0),
                getIntent().getDoubleExtra("list_total", 0),
                getIntent().getIntExtra("user_id", 1),
                getIntent().getStringExtra("cart_json")
        );


        viewModel.getIsLoading().observe(this, loading -> {
            btnCheckOut.setEnabled(!Boolean.TRUE.equals(loading));
            btnCheckOut.setText(Boolean.TRUE.equals(loading) ? "Đang xử lý..." : "Đặt hàng");
        });

        viewModel.getOrderResult().observe(this, mess -> {
            if (mess == null) return;
            if (mess.isSuccess()) {
                onOrderSuccess(mess.getOrder_id());
            } else {
                String msg = mess.getMessage() != null ? mess.getMessage() : "Đặt hàng thất bại";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });


        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });


        viewModel.getAddressFromGps().observe(this, address -> {
            if (address == null) return;
            isAutoFillFromLocation = true;
            editAddress.setText(address);
            isAutoFillFromLocation = false;
            Toast.makeText(this, "Đã cập nhật vị trí!", Toast.LENGTH_SHORT).show();
        });

        viewModel.getMomoPayEvent().observe(this, data -> {
            if (data == null) return;
            Intent intent = new Intent(this, PaymentWebViewActivity.class);
            intent.putExtra("pay_url",       data.payUrl);
            intent.putExtra("order_id",      data.orderId);
            intent.putExtra("redirect_host", MOMO_REDIRECT_HOST);
            intent.putExtra("redirect_path", MOMO_REDIRECT_PATH);
            startActivityForResult(intent, REQUEST_MOMO_PAY);
        });
    }

    private void setupClickListeners() {
        btnBackCheckOut.setOnClickListener(v -> finish());

        tvAddMore.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("open_tab", "store");
            intent.putExtra("IdCate", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        imgMarker.setOnClickListener(v -> {
            if (permissionHelper.hasPermission()) {
                Toast.makeText(this, "Chờ một chút để lấy địa chỉ của bạn...",
                        Toast.LENGTH_SHORT).show();
                viewModel.fetchLocation(locationHelper);
            } else {
                permissionHelper.requestPermission();
            }
        });

        cardPaymentMethod.setOnClickListener(v -> showPaymentOptions());
        dimOverlay.setOnClickListener(v -> hidePaymentOptions());

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

        btnCheckOut.setOnClickListener(v -> showConfirmDialog());
    }

    private void showConfirmDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);

        ((ImageView)  dialogView.findViewById(R.id.imgAction)).setImageResource(R.drawable.shopping_cart);
        ((TextView)   dialogView.findViewById(R.id.tvTitle)).setText("Xác nhận đơn hàng");
        ((TextView)   dialogView.findViewById(R.id.tvMessage)).setText("Vui lòng kiểm tra lại thông tin trước khi đặt hàng.");
        ((TextView)   dialogView.findViewById(R.id.tvInfoTitle)).setText("Phương thức thanh toán");
        ((TextView)   dialogView.findViewById(R.id.tvInfoValue)).setText(selectedPayment);

        androidx.appcompat.app.AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            dialog.dismiss();
            if (ppthanhtoan == 0) {
                viewModel.placeOrderCOD(
                        editAddress.getText().toString(),
                        edtNote.getText().toString(),
                        switchUtensils.isChecked(),
                        switchKetchup.isChecked(),
                        switchChili.isChecked()
                );
            } else {
                viewModel.requestMomoPayment(editAddress.getText().toString());
            }
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_MOMO_PAY) return;

        if (resultCode == RESULT_OK) {
            viewModel.placeOrderAfterMomo(
                    editAddress.getText().toString(),
                    edtNote.getText().toString(),
                    switchUtensils.isChecked(),
                    switchKetchup.isChecked(),
                    switchChili.isChecked()
            );
        } else {
            String error = (data != null) ? data.getStringExtra("error_message") : null;
            Toast.makeText(this,
                    error != null ? error : "Thanh toán đã bị hủy",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void onOrderSuccess(int orderId) {
        String statusPayment = (ppthanhtoan == 1) ? "Đã Thanh Toán" : "Chưa Thanh Toán";
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm",
                Locale.getDefault()).format(new Date());

        NotificationModel notif = new NotificationModel(
                orderId,
                "Đặt hàng thành công",
                "Đơn hàng của bạn đã được đặt thành công. Chúng tôi đang xử lý!",
                currentTime,
                tvTotalPriceCheckOut.getText().toString(),
                statusPayment,
                "success"
        );
        NotificationManager.addNotification(getApplicationContext(),
                getIntent().getIntExtra("user_id", 1), notif);

        new CartDAO(this).clearCart();
        Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

        getSharedPreferences("AppData", MODE_PRIVATE)
                .edit().putBoolean("need_show_badge", true).apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("SELECTED_ID", R.id.home);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updateBottomTotal() {
        tvTotalPriceCheckOut.setText(fmt.format(viewModel.getSaleTotal()) + " đ");
        double listTotal = viewModel.getListTotal();
        double saleTotal = viewModel.getSaleTotal();
        if (listTotal > saleTotal) {
            tvOldPrice.setVisibility(View.VISIBLE);
            tvOldPrice.setText(fmt.format(listTotal) + " đ");
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvOldPrice.setVisibility(View.GONE);
        }
    }

    private void setupAddressWatcher() {
        editAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (!isAutoFillFromLocation) viewModel.onAddressManuallyChanged();
            }
        });
    }

    private void showPaymentOptions() {
        footerCheckout.setVisibility(View.GONE);
        dimOverlay.setVisibility(View.VISIBLE);
        selectOptionPayment.setVisibility(View.VISIBLE);
        selectOptionPayment.post(() -> {
            selectOptionPayment.setTranslationY(selectOptionPayment.getHeight());
            selectOptionPayment.animate().translationY(0).setDuration(300)
                    .setInterpolator(new DecelerateInterpolator()).start();
        });
        dimOverlay.setAlpha(0f);
        dimOverlay.animate().alpha(1f).setDuration(300).start();
    }

    private void hidePaymentOptions() {
        footerCheckout.setVisibility(View.VISIBLE);
        selectOptionPayment.animate()
                .translationY(selectOptionPayment.getHeight() + 100)
                .setDuration(200).setInterpolator(new AccelerateInterpolator())
                .withEndAction(() -> selectOptionPayment.setVisibility(View.GONE))
                .start();
        dimOverlay.animate().alpha(0f).setDuration(200)
                .withEndAction(() -> dimOverlay.setVisibility(View.GONE))
                .start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Keyboard.hideKeyboardOnTouchOutside(this, event);
        return super.dispatchTouchEvent(event);
    }
}