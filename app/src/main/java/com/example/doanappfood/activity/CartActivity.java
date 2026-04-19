package com.example.doanappfood.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.databinding.ActivityCartBinding;
import com.example.doanappfood.model.CartItem;
import com.example.doanappfood.model.CartSauceItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CartActivity extends AppCompatActivity {
    private ImageView btnBackCart;
    private Button btnCheckout;
    private RecyclerView rvCart;
    private TextView tvTotalPrice, tvOldPrice;
    private CartAdapter adapter;
    private static final int CURRENT_USER_ID = 1;
    private CartDAO  cartDAO;
    private List<CartItem> cartItemList = new ArrayList<>();
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        cartDAO = new CartDAO(this);

        intitViews();
        setupRecyclerView();
        setupSwipeToDelete();
        loadCartData();

        btnBackCart.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckOutActivity.class);

                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
        });

    }
    @Override
    protected  void onResume(){
        super.onResume();
        loadCartData();
    }

    private void setupSwipeToDelete() {

        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    private final float BUTTON_WIDTH = 200f;
                    private final Paint paint = new Paint();
                    private final GradientDrawable background = new GradientDrawable();
                    {
                        background.setColor(Color.parseColor("#FF4D4D"));
                        float radius = 12 * getResources().getDisplayMetrics().density;
                        background.setCornerRadii(new float[]{
                                0,0,
                                radius,radius,
                                radius,radius,
                                0,0
                        });
                    }
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        CartItem item = cartItemList.get(position);
                        executor.execute(() -> {
                            cartDAO.removeItem(item.getId());
                            runOnUiThread(() -> {
                                cartItemList.remove(position);
                                adapter.notifyItemRemoved(position);
                                updateBottomTotal();
                            });
                        });
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c,
                                            @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY,
                                            int actionState, boolean isCurrentlyActive) {

                        View itemView = viewHolder.itemView;

                        float newDX = Math.max(-BUTTON_WIDTH, dX);

                        super.onChildDraw(c, recyclerView, viewHolder, newDX, dY,
                                actionState, isCurrentlyActive);

                        int right = itemView.getRight();
                        int top = itemView.getTop();
                        int bottom = itemView.getBottom();

                        background.setBounds(
                                right - (int) BUTTON_WIDTH,
                                top,
                                right,
                                bottom
                        );
                        background.draw(c);
                        Drawable icon = ContextCompat.getDrawable(
                                recyclerView.getContext(),
                                R.drawable.ic_delete
                        );
                        if (icon != null) {
                            int iconSize = 64;
                            int iconLeft = right - (int)(BUTTON_WIDTH / 2 + iconSize / 2);
                            int iconTop = top + (bottom - top) / 2 - iconSize / 2;
                            icon.setBounds(
                                    iconLeft,
                                    iconTop,
                                    iconLeft + iconSize,
                                    iconTop + iconSize
                            );
                            icon.draw(c);
                        }
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(rvCart);
    }

    private void intitViews() {
        btnBackCart = findViewById(R.id.btnBackCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        rvCart = findViewById(R.id.rvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvOldPrice = findViewById(R.id.tvOldPrice);
    }
    private void setupRecyclerView() {
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItemList, new CartAdapter.CartListener() {
            @Override
            public void onQuanityChange(int cartId, int quantity, int position) {
                executor.execute(() -> {
                    try{
                        cartDAO.updateItem(cartId, quantity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> updateBottomTotal());
                });
            }

            @Override
            public void onItemClick(CartItem item) {
                Intent intent = new Intent(CartActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_id", item.getProductId());
                intent.putExtra("product_name", item.getName());
                intent.putExtra("product_image", item.getImageUrl());
                intent.putExtra("product_price", item.getList_price());
                intent.putExtra("product_sale_price", item.getSale_price());
                intent.putExtra("current_quantity", item.getQuantity());

                ArrayList<String> selectedSauces = new ArrayList<>();
                if(item.getSauces() != null){
                    for(CartSauceItem s : item.getSauces()){
                        selectedSauces.add(s.getName());
                    }
                }
                intent.putStringArrayListExtra("selected_sauces", selectedSauces);
                intent.putExtra("cart_id", item.getId());
                intent.putExtra("is_update", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
        });
        rvCart.setAdapter(adapter);
    }
    private void loadCartData() {
        executor.execute(() -> {
            try {
                List<CartItem> fresh = cartDAO.getAll(CURRENT_USER_ID );
                runOnUiThread(() -> {
                    cartItemList.clear();
                    cartItemList.addAll(fresh);
                    adapter.notifyDataSetChanged();
                    updateBottomTotal();

                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void updateBottomTotal() {
        double saleTotal = 0, listTotal = 0;

        for (CartItem item : cartItemList) {
            int qty = item.getQuantity();
            double sale = item.getSale_price();
            double list = item.getList_price();

            saleTotal += (sale > 0 && sale < list ? sale : list) * qty;
            listTotal += list * qty;
        }

        tvTotalPrice.setText(fmt.format(saleTotal) + " đ");


        if (listTotal > saleTotal) {
            tvOldPrice.setVisibility(View.VISIBLE);
            tvOldPrice.setText(fmt.format(listTotal) + " đ");
            tvOldPrice.setPaintFlags(
                    tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvOldPrice.setVisibility(View.GONE);
        }
    }

}