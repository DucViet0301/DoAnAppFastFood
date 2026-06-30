package com.example.doanappfood.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.doanappfood.data.CartDAO;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.model.SaucesModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;
import com.example.doanappfood.repository.ProductDetailRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailViewModel extends AndroidViewModel {
    private  final MutableLiveData<ProductDetailModel> product = new MutableLiveData<>();
    private  final MutableLiveData<String> totalPriceText = new MutableLiveData<>();
    private  final  MutableLiveData<Boolean> cartSuccess = new MutableLiveData<>();
    private  final  MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private  final  MutableLiveData<String> saveText =  new MutableLiveData<>();


    private  int quantity = 1;
    private  double  activePrice = 0;
    private  double totalSaucesPrice = 0;
    private boolean isUpdateMode = false;
    private  int cartId = -1;
    private final ApiApp apiApp;
    private  final List<SaucesModel> salectSauces = new ArrayList<>();
    private final CartDAO cartDAO;
    private  final Executor executor = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Integer> quantityLiveData = new MutableLiveData<>(1);

    public LiveData<Integer> getQuantityLiveData() { return quantityLiveData; }

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        cartDAO = new CartDAO(application);
         apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }

    public LiveData<ProductDetailModel> getProduct()      { return product; }
    public LiveData<String> getTotalPriceText()           { return totalPriceText; }
    public LiveData<Boolean> getCartSuccess()             { return cartSuccess; }
    public LiveData<String> getErrorMessage()             { return errorMessage; }
    public LiveData<String> getSaveText()                 { return saveText; }
    public int getQuantity()                              { return quantity; }
    public  void init(boolean isUpdate, int cartId){
        this.isUpdateMode = isUpdate;
        this.cartId = cartId;
    }
    public void loadProduct(int productId, ArrayList<String> oldSauces, int oldQuantity) {
        apiApp.getProductDetail(productId).enqueue(new Callback<List<ProductDetailModel>>() {
            @Override
            public void onResponse(Call<List<ProductDetailModel>> call,
                                   Response<List<ProductDetailModel>> response) {
                if (!response.isSuccessful()
                        || response.body() == null
                        || response.body().isEmpty()) {
                    errorMessage.setValue("Không tải được sản phẩm");
                    return;
                }

                ProductDetailModel model = response.body().get(0);

                double list = model.getList_price();
                Double sale = model.getSale_price();
                boolean hasDiscount = sale != null && sale > 0 && sale < list;
                activePrice = hasDiscount ? sale : list;

                quantity = Math.max(1, oldQuantity);
                quantityLiveData.postValue(quantity);
                restoreOldSauces(model, oldSauces);

                product.setValue(model);
                recalcTotalInternal();

                if (hasDiscount) updateSaveText(list, sale);
            }

            @Override
            public void onFailure(Call<List<ProductDetailModel>> call, Throwable t) {
                Log.e("ProductDetailVM", "API error", t);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
    public void updateQuantity(int newQty) {
        quantity = Math.max(1, newQty);
        quantityLiveData.postValue(quantity);   // ← thêm dòng này
        recalcTotalInternal();
        ProductDetailModel m = product.getValue();
        if (m != null && m.getSale_price() != null) {
            updateSaveText(m.getList_price(), m.getSale_price());
        }
    }
    public void onSauceChanged(SaucesModel sauce, boolean isIncrease) {
        try {
            double price = Double.parseDouble(sauce.getPrice());
            if (isIncrease) {
                salectSauces.add(sauce);
                totalSaucesPrice += price;
            } else {
                salectSauces.remove(sauce);
                totalSaucesPrice -= price;
            }
            recalcTotalInternal();
        } catch (Exception e) {
            Log.e("ProductDetailVM", "Parse sauce price error", e);
        }
    }
    public void addOrUpdateCart(int userId) {
        ProductDetailModel model = product.getValue();
        if (model == null) return;

        executor.execute(() -> {
            try {
                double salePriceRaw = model.getSale_price() != null ? model.getSale_price() : 0;
                double listPriceRaw = model.getList_price();

                double finalSalePrice = salePriceRaw + totalSaucesPrice;
                double finalListPrice = listPriceRaw  + totalSaucesPrice;

                List<String> sauceNames = new ArrayList<>();
                for (SaucesModel s : salectSauces ){
                    sauceNames.add(s.getName());
                }

                if (isUpdateMode) {
                    cartDAO.updateFullItem(cartId, quantity, finalListPrice, finalSalePrice, sauceNames);
                } else {
                    String comboDetail = buildComboDetail(model);
                    cartDAO.addItem(
                            userId,
                            model.getId(),
                            model.getName(),
                            finalListPrice,
                            finalSalePrice,
                            quantity,
                            model.getImage(),
                            sauceNames,
                            comboDetail
                    );
                }
                cartSuccess.postValue(isUpdateMode);
            } catch (Exception e) {
                Log.e("ProductDetailVM", "Cart error", e);
                errorMessage.postValue("Đã có lỗi xảy ra");
            }
        });
    }
    private void recalcTotal(ProductDetailModel model) {
        recalcTotalInternal();
    }

    private void recalcTotalInternal() {
        double total = (activePrice + totalSaucesPrice) * quantity;
        String label = isUpdateMode
                ? "Cập nhật giỏ hàng: "
                : "Thêm giỏ hàng: ";
        String fmt = String.format(new java.util.Locale("vi", "VN"),
                label + "%,.0f đ", total);
        totalPriceText.postValue(fmt);
    }

    private void restoreOldSauces(ProductDetailModel model, ArrayList<String> oldSauces) {
        if (oldSauces == null || model.getSaucesModel() == null) return;
        for (String name : oldSauces) {
            for (SaucesModel s : model.getSaucesModel()) {
                if (s.getName().equals(name)) {
                    s.setQuantity(s.getQuantity() + 1);
                    salectSauces.add(s);
                    try { totalSaucesPrice += Double.parseDouble(s.getPrice()); }
                    catch (Exception ignored) {}
                    break;
                }
            }
        }
        recalcTotalInternal();
    }

    private void updateSaveText(double listPrice, double salePrice) {
        double saved = (listPrice - salePrice) * quantity;
        java.text.NumberFormat nf = java.text.NumberFormat
                .getNumberInstance(new java.util.Locale("vi", "VN"));
        saveText.postValue("Bạn tiết kiệm được " + nf.format(saved) + " sau khi giảm giá");
    }

    private String buildComboDetail(ProductDetailModel model) {
        if (!model.isCombo() || model.getProductModels() == null) return "";
        StringBuilder sb = new StringBuilder();
        for (ProductModel p : model.getProductModels()) {
            sb.append(p.getQuantity()).append(" x ").append(p.getName()).append(",");
        }
        return sb.toString().trim();
    }
}
