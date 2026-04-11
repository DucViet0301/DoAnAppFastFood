package com.example.doanappfood.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.adapter.CartAdapter;
import com.example.doanappfood.model.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Nút Back
        ImageView btnBackCart = findViewById(R.id.btnBackCart);
        if (btnBackCart != null) {
            btnBackCart.setOnClickListener(v -> finish());
        }

        // Nút Đặt hàng
        Button btnCheckout = findViewById(R.id.btnCheckout);
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            });
        }

        // Gạch ngang giá cũ
        TextView tvOldPrice = findViewById(R.id.tvOldPrice);
        if (tvOldPrice != null) {
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // === DANH SÁCH MÓN ĂN ===
        RecyclerView rvCartItems = findViewById(R.id.rvCartItems);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        List<CartItem> dummyList = new ArrayList<>();
        dummyList.add(new CartItem("Tiện Lợi 1", "1 x Tiện Lợi 1 (gà thường)\n1 x Pepsi (S)", 53000, 1, ""));
        dummyList.add(new CartItem("Gà Rán Yêu Thương 3", "1 x Vị yêu thương\n2 x Pepsi", 142000, 1, ""));
        dummyList.add(new CartItem("Khoai Tây Chiên", "Size L", 30000, 2, ""));

        CartAdapter adapter = new CartAdapter(dummyList);
        rvCartItems.setAdapter(adapter);

        // ====================================================================
        // ĐOẠN PHÉP THUẬT: KÉO SANG TRÁI -> HIỆN BẢNG CHỐNG "LỠ TAY"
        // ====================================================================
        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Lấy vị trí món ăn bị kéo
                int position = viewHolder.getAdapterPosition();

                // Hiện hộp thoại hỏi lại cho chắc chắn (Chống lỡ tay)
                new AlertDialog.Builder(CartActivity.this)
                        .setTitle("Xóa món ăn")
                        .setMessage("Bạn có chắc chắn muốn xóa món này khỏi giỏ hàng không?")
                        .setPositiveButton("Xóa luôn", (dialog, which) -> {
                            // Nếu chọn Xóa -> Bay màu
                            dummyList.remove(position);
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("Giữ lại", (dialog, which) -> {
                            // Nếu chọn Giữ lại -> Hồi sinh, trượt mượt mà về chỗ cũ
                            adapter.notifyItemChanged(position);
                        })
                        .setCancelable(false) // Bắt buộc phải bấm 1 trong 2 nút trên màn hình
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                ColorDrawable background = new ColorDrawable(Color.parseColor("#E53935"));
                Drawable deleteIcon = ContextCompat.getDrawable(CartActivity.this, R.drawable.ic_delete);

                if (deleteIcon != null) {
                    int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                    int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                    if (dX < 0) { // Kéo sang trái
                        int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                        int iconRight = itemView.getRight() - iconMargin;
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    } else {
                        background.setBounds(0, 0, 0, 0);
                        deleteIcon.setBounds(0, 0, 0, 0);
                    }

                    background.draw(c);
                    deleteIcon.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(rvCartItems);
    }
}