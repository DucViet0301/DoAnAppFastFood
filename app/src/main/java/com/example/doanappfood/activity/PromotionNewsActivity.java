package com.example.doanappfood.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.adapter.PromotionNewsAdapter;
import com.example.doanappfood.viewmodel.PromotionNewsViewModel;

import java.util.ArrayList;

public class PromotionNewsActivity extends AppCompatActivity {

    private RecyclerView rvPromotionNewsItems;
    private PromotionNewsAdapter adapter;
    private PromotionNewsViewModel viewModel;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_promotion_news);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initView();
        initViewModel();
    }

    private void initView() {
        rvPromotionNewsItems = findViewById(R.id.rvPromotionNewsItems);
        btnBack = findViewById(R.id.btnBack);

        rvPromotionNewsItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromotionNewsAdapter(new ArrayList<>(), this);
        rvPromotionNewsItems.setAdapter(adapter);

        // Bắt sự kiện click để mở chi tiết
        adapter.setOnPromotionNewsClickListener((promotionNewsModel, position) -> {
            android.content.Intent intent = new android.content.Intent(PromotionNewsActivity.this, com.example.doanappfood.activity.PromotionNewsDetailActivity.class);
            intent.putExtra("title", promotionNewsModel.getTitle());
            intent.putExtra("date", promotionNewsModel.getCreated_at());
            intent.putExtra("description", promotionNewsModel.getDescription());
            intent.putExtra("image", promotionNewsModel.getImage());
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(PromotionNewsViewModel.class);
        viewModel.getPromotionNewsList().observe(this, promotionNewsModels -> {
            if (promotionNewsModels != null) {
                adapter.setData(promotionNewsModels);
            }
        });
    }
}
