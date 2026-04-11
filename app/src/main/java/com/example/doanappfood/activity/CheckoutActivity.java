package com.example.doanappfood.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.adapter.CartAdapter;
import com.example.doanappfood.model.CartItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // NUT BACK
        ImageView btnBack = findViewById(R.id.btnBackCheckout);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // NUT THEM MON (Hien tai hien thong bao, sau nay se code chuyen trang)
        TextView tvAddMore = findViewById(R.id.tvAddMore);
        if (tvAddMore != null) {
            tvAddMore.setOnClickListener(v -> {
                Toast.makeText(CheckoutActivity.this, "Tinh nang them mon se duoc cap nhat sau", Toast.LENGTH_SHORT).show();
            });
        }

        // HIEN THI DANH SACH MON AN
        RecyclerView rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        if (rvCheckoutItems != null) {
            rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));

            // Do du lieu ao vao danh sach de test giao dien
            List<CartItem> dummyList = new ArrayList<>();
            dummyList.add(new CartItem("Tiện Lợi 1", "1 x Tiện Lợi 1 (gà thường)\n1 x Pepsi (S)", 53000, 1, ""));
            dummyList.add(new CartItem("Gà Rán Yêu Thương 3", "1 x Vị yêu thương\n2 x Pepsi", 142000, 1, ""));

            CartAdapter adapter = new CartAdapter(dummyList);
            rvCheckoutItems.setAdapter(adapter);
        }

        // BANG CHON PHUONG THUC THANH TOAN
        CardView cardPaymentMethod = findViewById(R.id.cardPaymentMethod);
        TextView tvSelectedPayment = findViewById(R.id.tvSelectedPayment);

        if (cardPaymentMethod != null) {
            cardPaymentMethod.setOnClickListener(v -> {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CheckoutActivity.this);

                View sheetView = LayoutInflater.from(CheckoutActivity.this).inflate(R.layout.layout_payment_bottom_sheet, null);
                bottomSheetDialog.setContentView(sheetView);

                LinearLayout layoutCOD = sheetView.findViewById(R.id.layoutCOD);
                layoutCOD.setOnClickListener(v1 -> {
                    tvSelectedPayment.setText("Tiền mặt (COD)");
                    tvSelectedPayment.setTextColor(Color.parseColor("#E53935"));
                    bottomSheetDialog.dismiss();
                });

                LinearLayout layoutMoMo = sheetView.findViewById(R.id.layoutMoMo);
                layoutMoMo.setOnClickListener(v12 -> {
                    tvSelectedPayment.setText("Ví MoMo");
                    tvSelectedPayment.setTextColor(Color.parseColor("#A50064"));
                    bottomSheetDialog.dismiss();
                });

                bottomSheetDialog.show();
            });
        }
    }
}