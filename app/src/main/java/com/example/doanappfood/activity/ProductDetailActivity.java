package com.example.doanappfood.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.adapter.ProductDetailComboAdapter;
import com.example.doanappfood.adapter.ProductDetailSaucesAdapter;
import com.example.doanappfood.databinding.ActivityProductDetailBinding;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.viewmodel.ProductDetailViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    private ProductDetailViewModel viewModel;
    private ActivityProductDetailBinding binding;
    private  int quantity = 1;
    private  double activePrice;
    private  ProductDetailModel currentModel;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        // Cấu hình UI tràn viền
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        setupViews();
        loadData();
    }

    private void setupViews() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        binding.ivback.setOnClickListener(v -> finish());

        // Config RecyclerViews
        binding.rvSauces.setLayoutManager(new LinearLayoutManager(this));
        binding.rvComboItems.setLayoutManager(new LinearLayoutManager(this));

        binding.rvSauces.setHasFixedSize(true);
        binding.rvComboItems.setHasFixedSize(true);
    }

    private void loadData() {
        int id = getIntent().getIntExtra("product_id", -1);
        if (id == -1) return;

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        viewModel.productDetailModelMutableLiveData(id).observe(this, model -> {
            if (model == null) return;
            this.currentModel = model;

            // 1. Hiển thị thông tin cơ bản
            binding.tvComboTitle.setText(model.getName());
            Glide.with(this).load(model.getImage()).into(binding.ivHeroBanner);

            // 2. Xử lý danh sách sauces
            if (model.getSaucesModel() != null && !model.getSaucesModel().isEmpty()) {
                binding.rvSauces.setAdapter(new ProductDetailSaucesAdapter(model.getSaucesModel()));
                binding.cardSauces.setVisibility(View.VISIBLE);
            } else {
                binding.cardSauces.setVisibility(View.GONE);
            }

            // 3. Xử lý danh sách combo items
            if (model.getProductModels() != null && !model.getProductModels().isEmpty()) {
                binding.rvComboItems.setAdapter(new ProductDetailComboAdapter(model.getProductModels()));
                binding.rvComboItems.setVisibility(View.VISIBLE);
            } else {
                binding.rvComboItems.setVisibility(View.GONE);
            }

            updateDiscountUI(model);
        });
    }
    private  void setupQuantityButtons(){
        binding.btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(quantity + 1);
            }
        });
        binding.btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(Math.max(1, quantity-1));
            }
        });
    }
    private  void updateQuantity(int newQuantity){
        quantity = newQuantity;
        binding.tvQuantity.setText(String.valueOf(quantity));
        binding.btnAddToCart.setText("Thêm giỏ hàng: " + fmt.format(activePrice * quantity) + " đ");

        if(currentModel != null && binding.tvsave.getVisibility() == View.VISIBLE){
            updateSaveText(currentModel.getList_price(), currentModel.getSale_price());
        }
    }

    private void updateDiscountUI(ProductDetailModel model) {
        double listPrice = model.getList_price();
        Double salePrice = model.getSale_price();

        boolean hasDiscount = salePrice != null && salePrice > 0 && salePrice < listPrice;
        activePrice = hasDiscount ? salePrice : listPrice;

        binding.tvPrice.setText(fmt.format(activePrice) + " đ");
        binding.btnAddToCart.setText("Thêm giỏ hàng: " + fmt.format(activePrice) + " đ");


        binding.layoutOriginalPrice.setVisibility(hasDiscount ? View.VISIBLE : View.GONE);
        binding.tvsave.setVisibility(hasDiscount ? View.VISIBLE : View.GONE);

        if (hasDiscount) {
            binding.tvOriginalPrice.setText(fmt.format(listPrice) + " đ");
            binding.tvOriginalPrice.setPaintFlags(
                    binding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            updateSaveText(listPrice, salePrice);
        }

        setupQuantityButtons();
    }
    private void updateSaveText(double listPrice, double salePrice) {
        String saved = fmt.format((listPrice - salePrice) * quantity);
        String full = "Bạn tiết kiệm được " + saved + " đ sau khi giảm giá";
        SpannableString span = new SpannableString(full);
        int start = full.indexOf(saved);
        if (start != -1) {
            span.setSpan(new ForegroundColorSpan(Color.RED),
                    start, start + saved.length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        binding.tvsave.setText(span);
    }
}