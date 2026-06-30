package com.example.doanappfood.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.network.ApiApp;
import com.example.doanappfood.network.RetrofitInstance;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean>   isLoading      = new MutableLiveData<>(false);
    private final MutableLiveData<MessModel> orderResult    = new MutableLiveData<>();
    private final MutableLiveData<String>    errorMessage   = new MutableLiveData<>();
    private final MutableLiveData<String>    addressFromGps = new MutableLiveData<>();
    private final MutableLiveData<MomoPayData> momoPayEvent = new MutableLiveData<>();


    private double saleTotal = 0;
    private double listTotal = 0;
    private int    userId    = 1;
    private String cartJson  = "";

    private double  finalTimeDelivery = 30;
    private String  distance          = "";
    private boolean isLocationFetched = false;

    private final OrderViewModel orderViewModel;
    private final ApiApp         apiApp;

    public CheckOutViewModel(@NonNull Application application) {
        super(application);
        orderViewModel = new OrderViewModel(application);
        orderViewModel.init();
        apiApp = RetrofitInstance.getRetrofit().create(ApiApp.class);
    }

    public LiveData<Boolean>      getIsLoading()      { return isLoading; }
    public LiveData<MessModel>    getOrderResult()    { return orderResult; }
    public LiveData<String>       getErrorMessage()   { return errorMessage; }
    public LiveData<String>       getAddressFromGps() { return addressFromGps; }
    public LiveData<MomoPayData>  getMomoPayEvent()   { return momoPayEvent; }
    public double getSaleTotal()                      { return saleTotal; }
    public double getListTotal()                      { return listTotal; }

    public void init(double saleTotal, double listTotal, int userId, String cartJson) {
        this.saleTotal = saleTotal;
        this.listTotal = listTotal;
        this.userId    = userId;
        this.cartJson  = cartJson;

        orderViewModel.getMessModelMutableLiveData().observeForever(mess -> {
            isLoading.postValue(false);
            if (mess != null) orderResult.postValue(mess);
        });
    }

    public void placeOrderCOD(String address, String note,
                              boolean utensils, boolean ketchup, boolean chili) {
        if (address.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập địa chỉ nhận hàng");
            return;
        }
        isLoading.setValue(true);
        buildAndSubmitOrder("Tiền mặt (COD)", address, note,
                utensils, ketchup, chili, false);
    }

    public void placeOrderAfterMomo(String address, String note,
                                    boolean utensils, boolean ketchup, boolean chili) {
        isLoading.setValue(true);
        buildAndSubmitOrder("Ví MoMo", address, note,
                utensils, ketchup, chili, true);
    }


    public void requestMomoPayment(String address) {
        if (address.trim().isEmpty()) {
            errorMessage.setValue("Vui lòng nhập địa chỉ nhận hàng");
            return;
        }
        isLoading.setValue(true);

        try {
            JSONObject cartBody = new JSONObject();
            cartBody.put("cart_items", new org.json.JSONArray(cartJson));

            RequestBody checkBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    cartBody.toString()
            );

            apiApp.checkCartValid(checkBody).enqueue(new Callback<MessModel>() {
                @Override
                public void onResponse(Call<MessModel> call, Response<MessModel> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Giỏ hàng hợp lệ -> tiến hành tạo thanh toán MoMo
                        createMomoPayment();
                    } else {
                        isLoading.postValue(false);
                        String msg = (response.body() != null && response.body().getMessage() != null)
                                ? response.body().getMessage()
                                : "Một số sản phẩm trong giỏ hàng không còn khả dụng";
                        errorMessage.postValue(msg);
                    }
                }

                @Override
                public void onFailure(Call<MessModel> call, Throwable t) {
                    isLoading.postValue(false);
                    errorMessage.postValue("Lỗi kiểm tra giỏ hàng: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            isLoading.postValue(false);
            errorMessage.postValue("Lỗi xử lý giỏ hàng");
        }
    }

    // Tách phần tạo thanh toán MoMo cũ ra hàm riêng
    private void createMomoPayment() {
        try {
            JSONObject body = new JSONObject();
            body.put("amount",      (long) saleTotal);
            body.put("orderInfo",   "Thanh toán đơn hàng FastFoodFour");
            body.put("redirectUrl", "https://payment.doanappfood.vn/result");

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    body.toString()
            );

            apiApp.createMomoPayment(requestBody).enqueue(new Callback<MessModel>() {
                @Override
                public void onResponse(Call<MessModel> call, Response<MessModel> response) {
                    isLoading.postValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        MessModel mess = response.body();
                        if (mess.isSuccess() && mess.getPayUrl() != null) {
                            momoPayEvent.postValue(
                                    new MomoPayData(mess.getPayUrl(),
                                            Integer.toString(mess.getOrder_id()))
                            );
                        } else {
                            errorMessage.postValue(
                                    mess.getMessage() != null ? mess.getMessage() : "Tạo đơn MoMo thất bại"
                            );
                        }
                    } else {
                        errorMessage.postValue("Server lỗi, thử lại sau");
                    }
                }

                @Override
                public void onFailure(Call<MessModel> call, Throwable t) {
                    isLoading.postValue(false);
                    errorMessage.postValue("Lỗi kết nối: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            isLoading.postValue(false);
            errorMessage.postValue("Lỗi tạo đơn MoMo");
        }
    }
    public void fetchLocation(com.example.doanappfood.Utlis.FusedLocationHelper locationHelper) {
        locationHelper.fetchAddress(
                new com.example.doanappfood.Utlis.FusedLocationHelper.OnAddressCallback() {
                    @Override public void onSuccess(String address) {
                        addressFromGps.setValue(address);
                    }
                    @Override public void onFailure(String message) {
                        errorMessage.setValue(message);
                    }
                });

        locationHelper.fetchLocation(
                new com.example.doanappfood.Utlis.FusedLocationHelper.OnLocationCallback() {
                    @Override public void onSuccess(double lat, double lng) {
                        locationHelper.getNearStore(lat, lng,
                                new com.example.doanappfood.Utlis.FusedLocationHelper.OnRouteCallback() {
                                    @Override
                                    public void onSuccess(String storeName, String dist, String duration) {
                                        try {
                                            double minutes = Double.parseDouble(
                                                    duration.replaceAll("[^0-9.]", ""));
                                            finalTimeDelivery = minutes + 15;
                                            isLocationFetched = true;
                                            distance = dist;
                                        } catch (Exception e) {
                                            finalTimeDelivery = 30;
                                        }
                                    }
                                    @Override public void onFailure(String message) {
                                        finalTimeDelivery = 30;
                                        errorMessage.setValue(message);
                                    }
                                });
                    }
                    @Override public void onFailure(String message) {
                        finalTimeDelivery = 30;
                        errorMessage.setValue(message);
                    }
                });
    }

    public void onAddressManuallyChanged() {
        finalTimeDelivery = 30;
        isLocationFetched = false;
    }


    private void buildAndSubmitOrder(String paymentMethod, String address, String note,
                                     boolean utensils, boolean ketchup, boolean chili,
                                     boolean momoSuccess) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("user_id",        userId);
        data.put("address",        address);
        data.put("payment_method", paymentMethod);
        data.put("time_delivery",  finalTimeDelivery);
        data.put("sale_total",     saleTotal);
        data.put("note",           note);
        data.put("distance",       distance);
        data.put("utensils",       utensils);
        data.put("ketchup",        ketchup);
        data.put("chili",          chili);
        data.put("cart_items",     new Gson().fromJson(cartJson, List.class));
        data.put("momo_success",   momoSuccess);
        orderViewModel.CheckOut(new Gson().toJson(data));
    }

    public static class MomoPayData {
        public final String payUrl;
        public final String orderId;
        public MomoPayData(String payUrl, String orderId) {
            this.payUrl  = payUrl;
            this.orderId = orderId;
        }
    }
}