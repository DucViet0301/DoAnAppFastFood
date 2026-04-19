package com.example.doanappfood.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;

public class PromotionNewsDetailActivity extends AppCompatActivity {

    private ImageView imgDetail, btnBack;
    private TextView tvTitleDetail, tvDescription, tvDateDetail;
    private Button btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion_news_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initView();
        setData();
    }

    private void initView() {
        imgDetail = findViewById(R.id.imgPromotionNewsDetail);
        btnBack = findViewById(R.id.btnBack);
        tvTitleDetail = findViewById(R.id.tvTitlePromotionNewsDetail);
        tvDescription = findViewById(R.id.tvDescriptionPromotionNews);
        btnBuy = findViewById(R.id.btnBuy);

        btnBack.setOnClickListener(v -> finish());

        btnBuy.setOnClickListener(v -> {
            // Phải quay lại MainActivity để MainActivity điều hướng sang StoreFragment
            Intent intent = new Intent(PromotionNewsDetailActivity.this, MainActivity.class);
            intent.putExtra("open_tab", "store");
            intent.putExtra("IdCate", 1);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void setData() {
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String image = getIntent().getStringExtra("image");

        tvTitleDetail.setText(title);
        tvDescription.setText(description);

        Glide.with(this)
                .load(image)
                .placeholder(R.drawable.ic_image)
                .into(imgDetail);
    }
}