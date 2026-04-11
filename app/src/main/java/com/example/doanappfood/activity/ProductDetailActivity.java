package com.example.doanappfood.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.adapter.ProductDetailComboAdapter;
import com.example.doanappfood.adapter.ProductDetailSaucesAdapter;
import com.example.doanappfood.databinding.ActivityProductDetailBinding;
import com.example.doanappfood.viewmodel.ProductDetailViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    ProductDetailViewModel viewModel;

    ActivityProductDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_product_detail);
        getView();
        getData();
    }
    private void getView(){
        binding.ivback.setOnClickListener(v -> finish());
        binding.rvSauces.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvSauces.setLayoutManager(layoutManager);
        binding.rvComboItems.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvComboItems.setLayoutManager(layoutManager1);

    }
    private void getData(){
        int id = getIntent().getIntExtra("product_id", -1);
        if (id == -1) return;
        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        viewModel.productDetailModelMutableLiveData(id).observe(this, productDetailModel -> {
            Log.d("Logg", productDetailModel.getName());

            // Xử lý sauces - ẩn section nếu không có data
            if (productDetailModel.getSaucesModel() != null && !productDetailModel.getSaucesModel().isEmpty()) {
                binding.rvSauces.setAdapter(new ProductDetailSaucesAdapter(productDetailModel.getSaucesModel()));
                binding.cardSauces.setVisibility(View.VISIBLE); // CardView chứa "Thêm sốt gà"
                binding.rvSauces.setVisibility(View.VISIBLE);
            } else {
                binding.cardSauces.setVisibility(View.GONE);
                binding.rvSauces.setVisibility(View.GONE);
            }

            // Xử lý combo items - ẩn section nếu không có data
            if (productDetailModel.getProductModels() != null && !productDetailModel.getProductModels().isEmpty()) {
                binding.rvComboItems.setAdapter(new ProductDetailComboAdapter(productDetailModel.getProductModels()));
                binding.rvComboItems.setVisibility(View.VISIBLE);
            } else {
                binding.rvComboItems.setVisibility(View.GONE);
            }

            NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            binding.tvComboTitle.setText(productDetailModel.getName());
            binding.tvOriginalPrice.setText(fmt.format(productDetailModel.getList_price()));

            if (productDetailModel.getSale_price() != null) {
                // Có giảm giá
                binding.tvPrice.setText(fmt.format(productDetailModel.getSale_price()));
                binding.btnAddToCart.setText("Thêm giỏ hàng: " + fmt.format(productDetailModel.getSale_price()) + " đ");
                String savedAmount = fmt.format(productDetailModel.getList_price() - productDetailModel.getSale_price());
                String fullText = "Bạn đã tiết kiệm được " + savedAmount + " đ sau khi giảm giá";

                SpannableString spannable = new SpannableString(fullText);
                int start = fullText.indexOf(savedAmount);
                int end = start + savedAmount.length()+2;
                spannable.setSpan(
                        new ForegroundColorSpan(Color.RED),
                        start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                binding.tvsave.setText(spannable);

                // Hiện giá gốc bị gạch
                binding.layoutOriginalPrice.setVisibility(View.VISIBLE);
                binding.tvOriginalPrice.setText(fmt.format(productDetailModel.getList_price()));
                binding.tvOriginalPrice.setPaintFlags(
                        binding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                );
            } else {
                // Không giảm giá - chỉ hiện giá gốc, ẩn icon + giá gạch
                binding.tvPrice.setText(fmt.format(productDetailModel.getList_price()));
                binding.btnAddToCart.setText("Thêm giỏ hàng: " + fmt.format(productDetailModel.getList_price()) + " đ");
                binding.tvsave.setVisibility(View.GONE);
                binding.layoutOriginalPrice.setVisibility(View.GONE); // ẩn cả icon lẫn giá gốc
            }

            Glide.with(this).load(productDetailModel.getImage()).into(binding.ivHeroBanner);
        });
    }
}