package com.example.doanappfood.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide; // Cậu nhớ để ý dòng này có bị đỏ không nhé
import com.example.doanappfood.R;

public class NewDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail);

        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDesc = findViewById(R.id.tvDetailDesc);
        ImageView imgDetail = findViewById(R.id.imgDetail);
        ImageView btnBack = findViewById(R.id.btnBackDetail);

        // Nút Back
        btnBack.setOnClickListener(v -> finish());

        String title = getIntent().getStringExtra("title");
        String desc = getIntent().getStringExtra("description");
        String image = getIntent().getStringExtra("image");

        if (title != null) {
            tvHeaderTitle.setText(title);
            tvTitle.setText(title);
        }

        if (desc != null) {
            tvDesc.setText(desc);
        }

        if (image != null && !image.isEmpty()) {
            Glide.with(this).load(image).into(imgDetail);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}