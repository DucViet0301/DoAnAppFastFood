package com.example.doanappfood.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.adapter.CartAdapter;
import com.example.doanappfood.model.CartItem;
import com.example.doanappfood.model.CartSauceItem;
import com.example.doanappfood.viewmodel.CartViewModel;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private CartViewModel viewModel;
    private SessionManager sessionManager;
    private List<CartItem> cartItemList = new ArrayList<>();
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        sessionManager = new SessionManager(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        intitViews();
        setupRecyclerView();
        setupSwipeToDelete();
        setupViewModel();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = new SessionManager(this).getUserId();
        viewModel.loadCartItems(userId);
    }
    private void intitViews() {
        btnBackCart = findViewById(R.id.btnBackCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        rvCart = findViewById(R.id.rvCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvOldPrice = findViewById(R.id.tvOldPrice);
    }
    public void setupViewModel(){
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // danh sacch items
        viewModel.getCartItems().observe(this, items -> {
            cartItemList.clear();
            cartItemList.addAll(items);
            adapter.notifyDataSetChanged();
        });

        // tong tien sale
        viewModel.getSaleTotal().observe(this, sale -> {
            tvTotalPrice.setText(fmt.format(sale) + " đ");
        });

        // gia goc
        viewModel.getListTotal().observe(this, list -> {
            Double sale = viewModel.getSaleTotal().getValue();
            if(sale != null && list > sale ){
                tvOldPrice.setVisibility(View.VISIBLE);
                tvOldPrice.setText(fmt.format(list) + " đ");
                tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else{
                tvOldPrice.setVisibility(View.GONE);
            }
        });
        viewModel.getCheckoutData().observe(this, data -> {
            if(data == null) return;
            Intent intent = new Intent(this, CheckOutActivity.class);
            intent.putExtra("sale_total", data.saleTotal);
            intent.putExtra("list_total", data.listTotal);
            intent.putExtra("user_id", data.userId);
            intent.putExtra("cart_json", data.cartJson);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        });
    }

    public  void setupClickListeners(){
        btnBackCart.setOnClickListener(v -> finish());
        btnCheckout.setOnClickListener(v -> {
            viewModel.prepareCheckout();
        });
    }

    public  void setupRecyclerView(){
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItemList, new CartAdapter.CartListener() {
            @Override
            public void onQuanityChange(int cartId, int quantity, int position) {
                viewModel.updateQuantity(cartId, quantity);
            }

            @Override
            public void onItemClick(CartItem item) {
                openProductDetail(item);
            }
        });
        rvCart.setAdapter(adapter);
    }
    public  void openProductDetail(CartItem item){
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id",        item.getProductId());
        intent.putExtra("product_name",      item.getName());
        intent.putExtra("product_image",     item.getImageUrl());
        intent.putExtra("product_price",     item.getList_price());
        intent.putExtra("product_sale_price", item.getSale_price());
        intent.putExtra("current_quantity",  item.getQuantity());
        intent.putExtra("cart_id",           item.getId());
        intent.putExtra("is_update",         true);
        ArrayList<String> selectSauces = new ArrayList<>();
        if(item.getSauces() != null){
            for(CartSauceItem s : item.getSauces()){
                for(int i = 0 ; i< s.getQuantity(); i++){
                    selectSauces.add(s.getName());
                }
            }
        }
        intent.putStringArrayListExtra("selected_sauces", selectSauces);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback callback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                    private final float BUTTON_WIDTH = 200f;
                    private final GradientDrawable background = new GradientDrawable();

                    {
                        background.setColor(Color.parseColor("#FF4D4D"));
                        float radius = 12 * getResources().getDisplayMetrics().density;
                        background.setCornerRadii(new float[]{0, 0, radius, radius, radius, radius, 0, 0});
                    }

                    @Override
                    public boolean onMove(@NonNull RecyclerView rv,
                                          @NonNull RecyclerView.ViewHolder vh,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        CartItem item = cartItemList.get(position);
                        viewModel.removeItems(item.getId(), position);
                    }

                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView rv,
                                            @NonNull RecyclerView.ViewHolder vh,
                                            float dX, float dY, int actionState, boolean isActive) {
                        View itemView = vh.itemView;
                        float newDX = Math.max(-BUTTON_WIDTH, dX);
                        super.onChildDraw(c, rv, vh, newDX, dY, actionState, isActive);

                        int right = itemView.getRight();
                        background.setBounds(
                                right - (int) BUTTON_WIDTH, itemView.getTop(),
                                right, itemView.getBottom());
                        background.draw(c);

                        Drawable icon = ContextCompat.getDrawable(rv.getContext(), R.drawable.ic_delete);
                        if (icon != null) {
                            int iconSize = 64;
                            int iconLeft = right - (int)(BUTTON_WIDTH / 2 + iconSize / 2f);
                            int iconTop  = itemView.getTop() + (itemView.getBottom() - itemView.getTop()) / 2 - iconSize / 2;
                            icon.setBounds(iconLeft, iconTop, iconLeft + iconSize, iconTop + iconSize);
                            icon.draw(c);
                        }
                    }
                };

        new ItemTouchHelper(callback).attachToRecyclerView(rvCart);
    }
}