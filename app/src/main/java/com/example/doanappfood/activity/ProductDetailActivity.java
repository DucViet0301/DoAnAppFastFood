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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.doanappfood.R;
import com.example.doanappfood.adapter.ProductDetailComboAdapter;
import com.example.doanappfood.adapter.ProductDetailSaucesAdapter;
import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.databinding.ActivityProductDetailBinding;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.model.SaucesModel;
import com.example.doanappfood.viewmodel.ProductDetailViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDetailViewModel viewModel;
    private ProductDetailSaucesAdapter saucesAdapter;

    private int quantity = 1;
    private double totalSaucesPrice = 0;
    private static final int CURRENT_USER_ID = 1;
    private int CartId;
    private double activePrice = 0;
    private  boolean isUpdateMode = false;

    private ProductDetailModel currentModel;
    private List<SaucesModel> selectedSaucesList = new ArrayList<>();

    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setupUI();
        showInstantData();
        loadData();
    }

    private void setupUI() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        binding.ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        binding.rvSauces.setLayoutManager(new LinearLayoutManager(this));
        binding.rvComboItems.setLayoutManager(new LinearLayoutManager(this));

        binding.btnIncrease.setOnClickListener(v -> updateQuantity(quantity + 1));
        binding.btnDecrease.setOnClickListener(v -> updateQuantity(Math.max(1, quantity - 1)));
    }
    public  void showInstantData(){
        String name = getIntent().getStringExtra("product_name");
        String image = getIntent().getStringExtra("product_image");
        double sale = getIntent().getDoubleExtra("product_sale_price", 0);
        double list = getIntent().getDoubleExtra("product_price", 0);
        int oldQuantity = getIntent().getIntExtra("current_quantity", 1);
        binding.tvComboTitle.setText(name);
        Glide.with(this).load(image).into(binding.ivHeroBanner);
        quantity = oldQuantity;
        binding.tvQuantity.setText(String.valueOf(quantity));
        if(sale > 0){
            activePrice = sale;
        }
        else{
            activePrice = list;
        }
        binding.tvPrice.setText(fmt.format(activePrice) + " đ");
        updateTotalPrice();
    }

    private void loadData() {
        int id = getIntent().getIntExtra("product_id", -1);
        isUpdateMode = getIntent().getBooleanExtra("is_update", false);
        CartId = getIntent().getIntExtra("cart_id", -1);

        ArrayList<String> oldSauces = getIntent().getStringArrayListExtra("selected_sauces");
        int oldQuantity = getIntent().getIntExtra("current_quantity", 1);
        if (id == -1) return;

        viewModel = new ViewModelProvider(this).get(ProductDetailViewModel.class);
        viewModel.productDetailModelMutableLiveData(id).observe(this, model -> {
            if (model == null) return;
            currentModel = model;

            updateQuantity(oldQuantity);
            binding.tvComboTitle.setText(model.getName());
            Glide.with(this).load(model.getImage()).into(binding.ivHeroBanner);

            setupSauces(model, oldSauces);
            setupCombo(model);
            updatePriceUI(model);
            setupAddToCart();
        });
    }

    private void setupSauces(ProductDetailModel model, ArrayList<String> sauces) {
        if (model.getSaucesModel() != null && !model.getSaucesModel().isEmpty()) {

            saucesAdapter = new ProductDetailSaucesAdapter(model.getSaucesModel());
            binding.rvSauces.setAdapter(saucesAdapter);

            if(sauces != null){
                for(SaucesModel sauceAPI : model.getSaucesModel()){
                    for(String oldName: sauces){
                        if(sauceAPI.getName().equals(oldName)){
                            selectedSaucesList.add(sauceAPI);
                            sauceAPI.setQuantity(sauceAPI.getQuantity() + 1);
                            try{
                                totalSaucesPrice += Double.parseDouble(sauceAPI.getPrice());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                updateTotalPrice();
            }

            saucesAdapter.setOnProductSaucesClickListener((sauce,  isIncreate) -> {
                try {
                    double price = Double.parseDouble(sauce.getPrice());
                    if(isIncreate){
                        selectedSaucesList.add(sauce);
                        totalSaucesPrice += price;
                    }
                    else{
                        selectedSaucesList.remove(sauce);
                        totalSaucesPrice -= price;
                    }
                    updateTotalPrice();
                } catch (Exception e) {
                    Log.e("SAUCE_ERROR", "Parse price lỗi", e);
                }
            });

            binding.cardSauces.setVisibility(View.VISIBLE);
        } else {
            binding.cardSauces.setVisibility(View.GONE);
        }
    }

    private void setupCombo(ProductDetailModel model) {
        if (model.getProductModels() != null && !model.getProductModels().isEmpty()) {
            binding.rvComboItems.setAdapter(new ProductDetailComboAdapter(model.getProductModels()));
            binding.rvComboItems.setVisibility(View.VISIBLE);
        } else {
            binding.rvComboItems.setVisibility(View.GONE);
        }
    }

    private void updatePriceUI(ProductDetailModel model) {
        double listPrice = model.getList_price();
        Double salePrice = model.getSale_price();

        boolean hasDiscount = salePrice != null && salePrice > 0 && salePrice < listPrice;
        activePrice = hasDiscount ? salePrice : listPrice;

        binding.tvPrice.setText(fmt.format(activePrice) + " đ");

        if (hasDiscount) {
            binding.tvOriginalPrice.setText(fmt.format(listPrice) + " đ");
            binding.tvOriginalPrice.setPaintFlags(
                    binding.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            binding.layoutOriginalPrice.setVisibility(View.VISIBLE);
            binding.tvsave.setVisibility(View.VISIBLE);

            updateSaveText(listPrice, salePrice);
        } else {
            binding.layoutOriginalPrice.setVisibility(View.GONE);
            binding.tvsave.setVisibility(View.GONE);
        }

        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = (activePrice + totalSaucesPrice) * quantity;
        if (isUpdateMode){
            binding.btnAddToCart.setText("Cập nhật giỏ hàng: " + fmt.format(total) + " đ");
        }else {
            binding.btnAddToCart.setText("Thêm giỏ hàng: " + fmt.format(total) + " đ");
        }
    }

    private void updateQuantity(int newQuantity) {
        quantity = newQuantity;
        binding.tvQuantity.setText(String.valueOf(quantity));
        updateTotalPrice();
        if (currentModel != null && currentModel.getSale_price() != null) {
            updateSaveText(currentModel.getList_price(), currentModel.getSale_price());
        }
    }

    private void updateSaveText(double listPrice, double salePrice) {
        double totalSaved = (listPrice - salePrice) * quantity;
        String saved = fmt.format(totalSaved);
        String full = "Bạn tiết kiệm được " + saved + " sau khi giảm giá";

        SpannableString span = new SpannableString(full);
        int start = full.indexOf(saved);

        if (start != -1) {
            span.setSpan(new ForegroundColorSpan(Color.RED),
                    start, start + saved.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        binding.tvsave.setText(span);
    }

    private void setupAddToCart() {
        Executor executor = Executors.newSingleThreadExecutor();

        binding.btnAddToCart.setOnClickListener(v -> {
            if (currentModel == null) return;

            List<String> selectedSauceName = new ArrayList<>();
            if (saucesAdapter != null) {
                for (SaucesModel sauce : selectedSaucesList) {
                    selectedSauceName.add(sauce.getName());
                }
            }
            executor.execute(() -> {
                try {
                    double salePriceRaw = (currentModel.getSale_price() != null) ? currentModel.getSale_price() : 0;
                    double listPriceRaw = currentModel.getList_price();

                    StringBuilder productInfo = new StringBuilder();
                    // Trong setupAddToCart()
                    String comboDetail = "";
                    if (currentModel.isIs_combo()) {
                        for (ProductModel p : currentModel.getProductModels()) {
                            productInfo.append(p.getQuantity()).append(" x ").append(p.getName()).append(",");
                        }
                        comboDetail = productInfo.toString().trim();
                    } else {
                        productInfo.append(currentModel.getName());
                    }


                    double finalSalePrice = (salePriceRaw + totalSaucesPrice) * quantity;
                    double finalListPrice = (listPriceRaw + totalSaucesPrice) * quantity;

                    CartDAO cartDAO = new CartDAO(this);
                    if(isUpdateMode){
                       cartDAO.updateFullItem(CartId, quantity, finalListPrice,
                               finalSalePrice,selectedSauceName);
                    }else{
                        cartDAO.addItem(CURRENT_USER_ID , currentModel.getId(), currentModel.getName(), finalListPrice,
                                finalSalePrice, quantity, currentModel.getImage(),
                                selectedSauceName, comboDetail
                        );
                    }

                    Log.d("CART_DEBUG", "========= GIỎ HÀNG =========");
                    Log.d("CART_DEBUG", "Sản phẩm: " + productInfo.toString());
                    Log.d("CART_DEBUG", "Cart: " + CartId);
                    Log.d("CART_DEBUG", "Sot: " + selectedSauceName.toString());
                    Log.d("CART_DEBUG", "Giá niêm yết: " + fmt.format(finalListPrice) + " đ");
                    Log.d("CART_DEBUG", "Giá sale:     " + fmt.format(finalSalePrice) + " đ");
                    Log.d("CART_DEBUG", "Số lượng: " + quantity);
                    Log.d("DEBUG", "is_combo = " + currentModel.isIs_combo());

                    runOnUiThread(() -> {
                        Toast.makeText(this, isUpdateMode ? "Cập nhật giỏ hàng thành công!" : "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                    });

                } catch (Exception e) {
                    e.printStackTrace();

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }
}