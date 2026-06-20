package com.example.doanappfood.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.Utlis.SessionManager;
import com.example.doanappfood.adapter.ProductDetailComboAdapter;
import com.example.doanappfood.adapter.ProductDetailSaucesAdapter;
import com.example.doanappfood.databinding.ActivityProductDetailBinding;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.viewmodel.ProductDetailViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel viewModel;
    private com.example.doanappfood.Utlis.SessionManager sessionManager;

    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        sessionManager = new SessionManager(this);

        setupUI();
        setupViewModel();
        loadInitialData();
    }

    private void setupUI() {
        binding.rvSauces.setLayoutManager(new LinearLayoutManager(this));
        binding.rvComboItems.setLayoutManager(new LinearLayoutManager(this));

        binding.ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        binding.btnIncrease.setOnClickListener(v ->
                viewModel.updateQuantity(viewModel.getQuantity() + 1));

        binding.btnDecrease.setOnClickListener(v ->
                viewModel.updateQuantity(viewModel.getQuantity() - 1));

        binding.btnAddToCart.setOnClickListener(v -> handleAddToCart());
    }
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);

        boolean isUpdate = getIntent().hasExtra("cart_id");
        int cartId = getIntent().getIntExtra("cart_id", -1);
        viewModel.init(isUpdate, cartId);

        viewModel.getProduct().observe(this, this::bindProduct);


        viewModel.getTotalPriceText().observe(this, text ->
                binding.btnAddToCart.setText(text));


        viewModel.getSaveText().observe(this, saveStr -> {
            if (saveStr == null) return;

            SpannableString span = new SpannableString(saveStr);

            int start = saveStr.indexOf("được ") + 5;
            int end   = saveStr.indexOf(" sau");
            if (start > 4 && end > start) {
                span.setSpan(new ForegroundColorSpan(Color.RED),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            binding.tvsave.setText(span);
        });

        viewModel.getCartSuccess().observe(this, isUpdate2 -> {
            String msg = Boolean.TRUE.equals(isUpdate2)
                    ? "Cập nhật giỏ hàng thành công!"
                    : "Đã thêm vào giỏ hàng!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        // Observe lỗi
        viewModel.getErrorMessage().observe(this, err -> {
            if (err != null) Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
        });
    }
    private void loadInitialData() {
        String name  = getIntent().getStringExtra("product_name");
        String image = getIntent().getStringExtra("product_image");
        double sale  = getIntent().getDoubleExtra("product_sale_price", 0);
        double list  = getIntent().getDoubleExtra("product_price", 0);

        binding.tvComboTitle.setText(name);
        Glide.with(this).load(image).into(binding.ivHeroBanner);
        binding.tvPrice.setText(fmt.format(sale > 0 ? sale : list) + " đ");


        int productId    = getIntent().getIntExtra("product_id", -1);
        int oldQuantity  = getIntent().getIntExtra("current_quantity", 1);
        ArrayList<String> oldSauces = getIntent().getStringArrayListExtra("selected_sauces");

        if (productId != -1) {
            viewModel.loadProduct(productId, oldSauces, oldQuantity);
        }
    }
    private void bindProduct(ProductDetailModel model) {
        if (model == null) return;

        binding.tvComboTitle.setText(model.getName());
        Glide.with(this).load(model.getImage()).into(binding.ivHeroBanner);
        binding.tvQuantity.setText(String.valueOf(viewModel.getQuantity()));

        bindPriceUI(model);
        bindSauces(model);
        bindCombo(model);
    }
    private void bindPriceUI(ProductDetailModel model) {
        double listPrice = model.getList_price();
        Double salePrice = model.getSale_price();
        boolean hasDiscount = salePrice != null && salePrice > 0 && salePrice < listPrice;

        binding.tvPrice.setText(fmt.format(hasDiscount ? salePrice : listPrice) + " đ");

        if (hasDiscount) {
            binding.tvOriginalPrice.setText(fmt.format(listPrice) + " đ");
            binding.tvOriginalPrice.setPaintFlags(
                    binding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.layoutOriginalPrice.setVisibility(View.VISIBLE);
            binding.tvsave.setVisibility(View.VISIBLE);
        } else {
            binding.layoutOriginalPrice.setVisibility(View.GONE);
            binding.tvsave.setVisibility(View.GONE);
        }
    }

    private void bindSauces(ProductDetailModel model) {
        if (model.getSaucesModel() != null && !model.getSaucesModel().isEmpty()) {
            ProductDetailSaucesAdapter saucesAdapter =
                    new ProductDetailSaucesAdapter(model.getSaucesModel());

            saucesAdapter.setOnProductSaucesClickListener((sauce, isIncrease) ->
                    viewModel.onSauceChanged(sauce, isIncrease));

            binding.rvSauces.setAdapter(saucesAdapter);
            binding.cardSauces.setVisibility(View.VISIBLE);
        } else {
            binding.cardSauces.setVisibility(View.GONE);
        }
    }

    private void bindCombo(ProductDetailModel model) {
        if (model.getProductModels() != null && !model.getProductModels().isEmpty()) {
            binding.rvComboItems.setAdapter(
                    new ProductDetailComboAdapter(model.getProductModels()));
            binding.rvComboItems.setVisibility(View.VISIBLE);
        } else {
            binding.rvComboItems.setVisibility(View.GONE);
        }
    }
    private void handleAddToCart() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return;
        }
        viewModel.addOrUpdateCart(sessionManager.getUserId());
    }
}