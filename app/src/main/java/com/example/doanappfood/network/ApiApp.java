package com.example.doanappfood.network;

import com.example.doanappfood.model.AuthModel;
import com.example.doanappfood.model.BannerModel;
import com.example.doanappfood.model.CategoryModel;
import com.example.doanappfood.model.ChangePassModel;
import com.example.doanappfood.model.ComboModel;
import com.example.doanappfood.model.DirectionModel;
import com.example.doanappfood.model.LoginModel;
import com.example.doanappfood.model.MessModel;
import com.example.doanappfood.model.NewModel;
import com.example.doanappfood.model.OrderDetailModel;
import com.example.doanappfood.model.OrderModel;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.model.PromotionNewsModel;
import com.example.doanappfood.model.RegisterModel;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.model.StoreModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiApp {

    @GET("banners")
    Call<List<BannerModel>> getBanner();

    @GET("combos")
    Call<List<ComboModel>> getCombos();

    @GET("news")
    Call<List<NewModel>> getNew();

    @GET("promotionnews")
    Call<List<PromotionNewsModel>> getPromotionNews();

    @GET("category")
    Call<List<CategoryModel>> getCategory();


    @GET("products/{id}")
    Call<List<ProductModel>> getProduct(@Path("id") int idCate);

    @GET("products/detail/{id}")
    Call<List<ProductDetailModel>> getProductDetail(@Path("id") int id);

    @GET("store")
    Call<List<StoreModel>> getAllStores();

    @GET("store/nearest")
    Call<StoreModel> getNearestStore(
            @Query("lat") double lat,
            @Query("lng") double lng
    );

    @GET("store/direction")
    Call<DirectionModel> getDirections(
            @Query("fromLat") double fromLat,
            @Query("fromLng") double fromLng,
            @Query("toLat") double toLat,
            @Query("toLng") double toLng
    );
    @POST("chatbot")
    Call<Map<String, String>> sendMessage(@Body Map<String, String> body);

    @POST("auth/logout")
    Call<AuthModel> logout();
    @POST("auth/login")
    Call<AuthModel> login(@Body LoginModel request);
    @POST("auth/register")
    Call<AuthModel> register(@Body RegisterModel request);

    @POST("auth/changepassword")
    Call<ResponseModel> changePassword(
            @Body ChangePassModel body
    );
    @FormUrlEncoded
    @POST("auth/refresh-token")
    Call<AuthModel> refreshToken(@Field("refresh_token") String refreshToken);
    // momo
    @POST("payment")
    Call<MessModel> createMomoPayment(@Body okhttp3.RequestBody body);
    @POST("order")
    Call<MessModel> postOrder(@Body okhttp3.RequestBody detail);
    @GET("order/{id}")
    Call<List<OrderModel>> getAllOrder(@Path("id") int userId);

    @PUT("order/cancel/{id}")
    Call<MessModel> cancelOrder(@Path("id") int orderId);
    @GET("order/detail/{id}")
    Call<OrderDetailModel> getOrderDetail(@Path("id") int orderId);

}