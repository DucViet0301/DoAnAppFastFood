package com.example.doanappfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanappfood.R;
import com.example.doanappfood.adapter.NewAdapter;
import com.example.doanappfood.viewmodel.NewViewModel;

import java.util.ArrayList;

public class AllNewsActivity extends AppCompatActivity {
    private NewAdapter newAdapter;
    private RecyclerView rcvAllNewsList;
    private NewViewModel newViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_news);

        ImageView btnBack = findViewById(R.id.btnBackAll);
        btnBack.setOnClickListener(v -> finish());

        rcvAllNewsList = findViewById(R.id.rcvAllNewsList);

        android.util.DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;

        int noOfColumns = (int) (screenWidthDp / 300);

        if (noOfColumns < 1) {
            noOfColumns = 1;
        }

        rcvAllNewsList.setLayoutManager(new GridLayoutManager(this, noOfColumns));

        newAdapter = new NewAdapter(new ArrayList<>(), this);
        newAdapter.setLayoutResource(R.layout.item_new_vertical);
        rcvAllNewsList.setAdapter(newAdapter);

        newViewModel = new ViewModelProvider(this).get(NewViewModel.class);
        newViewModel.getNewList().observe(this, newModels -> {
            if (newModels != null) {
                newAdapter.setData(newModels);
            }
        });

        newAdapter.setOnNewClickListener((newModel, position) -> {
            Intent intent = new Intent(AllNewsActivity.this, NewDetailActivity.class);
            intent.putExtra("new_id", newModel.getId());
            intent.putExtra("title", newModel.getTitle());
            intent.putExtra("description", newModel.getDescription());
            intent.putExtra("image", newModel.getImage());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}