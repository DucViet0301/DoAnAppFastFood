package com.example.doanappfood.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.DateUtils;
import com.example.doanappfood.Utlis.NotificationBadgeUtlis;
import com.example.doanappfood.Utlis.NotificationManager;
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.adapter.OrderDetailAdapter;
import com.example.doanappfood.model.NotificationModel;
import com.example.doanappfood.viewmodel.OrderDetailViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {
    private com.example.doanappfood.Utlis.SessionManager sessionManager;
    private androidx.appcompat.widget.Toolbar toolbar;
    private int orderId;
    private RecyclerView rvProductList;
    private View lineproduct;
    private int userId;

    private OrderDetailViewModel viewModel;
    private TextView tvCustomerName, tvPhoneNumber, tvDeliveryAddress, tvOrderId;
    private TextView tvTotalItemCount, tvStatus, tvOrderTime;
    private TextView tvNoteUtensils, tvNoteChili, tvNoteKetchup, tvNoteDetail, tvTotalPrice;
    private LinearLayout noteOrder;
    private com.google.android.material.button.MaterialButton btnCancelOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);
        orderId = getIntent().getIntExtra("order_id", -1);
        if (orderId == -1) finish();

        initViews();
        setupToolbar();
        setupViewModel();
        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();


    }

    private void initViews() {
        tvCustomerName = findViewById(R.id.tvCustomerName);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvDeliveryAddress = findViewById(R.id.tvDeliveryAddress);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvTotalItemCount = findViewById(R.id.tvTotalItemCount);
        tvStatus = findViewById(R.id.tvStatus);
        tvOrderTime = findViewById(R.id.tvOrderTime);
        tvNoteUtensils = findViewById(R.id.tvNoteUtensils);
        tvNoteChili = findViewById(R.id.tvNoteChili);
        tvNoteKetchup = findViewById(R.id.tvNoteKetchup);
        tvNoteDetail = findViewById(R.id.tvNoteDetail);
        noteOrder = findViewById(R.id.noteOrder);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        lineproduct = findViewById(R.id.lineproduct);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);


        rvProductList = findViewById(R.id.rvProductList);
        rvProductList.setLayoutManager(new LinearLayoutManager(this));
        rvProductList.setNestedScrollingEnabled(false);
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(OrderDetailViewModel.class);
        loadOrderDetail();
    }

    private void loadOrderDetail() {
        viewModel.getOrderDetail(orderId).observe(this, order -> {
            if (order == null) {
                Toast.makeText(this, "Không tải được đơn hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            tvCustomerName.setText(order.getCustomer_name());
            tvPhoneNumber.setText(order.getPhone_number());
            tvDeliveryAddress.setText(order.getAddress());

            tvOrderId.setText("#Mã đơn hàng-DV" + order.getId());
            tvTotalItemCount.setText(order.getProducts().size() + " sản phẩm");
            if (order.getStatus().equals("Đang xử lý")) {
                tvStatus.setText("Chưa Thanh Toán");
            } else if (order.getStatus().equals("cancelled")) {
                tvStatus.setText("Đã hủy đơn hàng");
                btnCancelOrder.setVisibility(View.GONE);
            } else if (order.getStatus().equals("Đã thanh toán")) {
                tvStatus.setText("Đã Thanh Toán");
            }
            tvOrderTime.setText(DateUtils.formatDate(order.getCreated_at()));
            tvTotalPrice.setText(formatPrice(order.getTotal_price()));
            OrderDetailAdapter adapter = new OrderDetailAdapter(this, order.getProducts());
            rvProductList.setAdapter(adapter);


            boolean hasUtensils = order.getIs_dungcu();
            boolean hasKetchup = order.getIs_tuongca();
            boolean hasChili = order.getIs_tuongot();
            String note = order.getNote();


            if (!hasUtensils && !hasKetchup && !hasChili
                    && (note == null || note.trim().isEmpty())) {

                noteOrder.setVisibility(View.GONE);
                lineproduct.setVisibility(View.GONE);

            } else {
                tvNoteUtensils.setVisibility(hasUtensils ? View.VISIBLE : View.GONE);
                tvNoteKetchup.setVisibility(hasKetchup ? View.VISIBLE : View.GONE);
                tvNoteChili.setVisibility(hasChili ? View.VISIBLE : View.GONE);
                if (hasUtensils) {
                    tvNoteUtensils.setText("- Lấy dụng cụ ăn uống");
                }
                if (hasKetchup) {
                    tvNoteKetchup.setText("- Lấy tương cà");
                }
                if (hasChili) {
                    tvNoteChili.setText("- Lấy tương ớt");
                }
                if (note != null && !note.trim().isEmpty()) {
                    tvNoteDetail.setText("- Ghi chú: " + note);
                } else {
                    tvNoteDetail.setVisibility(View.GONE);
                }
            }
            btnCancelOrder.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Xác nhận hủy đơn")
                        .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này?")
                        .setPositiveButton("Đồng ý", (dialog, which) -> {
                            viewModel.cancel(orderId).observe(this, mess -> {
                                if (mess != null && mess.isSuccess()) {
                                    Toast.makeText(this, "Đã huỷ đơn hàng", Toast.LENGTH_SHORT).show();
                                    String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
                                    NotificationModel newNotif = new NotificationModel(
                                            orderId,
                                            "Hủy đơn thành công",
                                            "Đơn hàng #" + orderId + " của bạn đã được hủy thành công.",
                                            currentTime,
                                            tvTotalPrice.getText().toString(),
                                            "Đã hủy",
                                            "cancel"
                                    );
                                    NotificationManager.addNotification(this, userId, newNotif);
                                    order.setStatus("cancelled");
                                    getSharedPreferences("AppData", MODE_PRIVATE)
                                            .edit()
                                            .putBoolean("need_show_badge", true)
                                            .apply();
                                    tvStatus.setText("Đã huỷ đơn hàng");
                                    btnCancelOrder.setVisibility(View.GONE);
                                    Intent intent = new Intent();
                                    intent.putExtra("updated", true);
                                    setResult(RESULT_OK, intent);
                                } else {
                                    String msg = (mess != null) ? mess.getMessage() : "Huỷ đơn thất bại";
                                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Không", null)
                        .show();
            });
        });

    }

    private String formatPrice(double price) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        return nf.format((long) price) + " đ";
    }
}