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

    @FormUrlEncoded
    @POST("products")
    Call<List<ProductModel>> getProduct(@Field("idCate") int idCate);

    @FormUrlEncoded
    @POST("productdetails")
    Call<List<ProductDetailModel>> getProductDetail(@Field("idProduct") int id);

    @GET("stores")
    Call<List<StoreModel>> getAllStores();

    @GET("stores/nearest")
    Call<StoreModel> getNearestStore(
            @Query("lat") double lat,
            @Query("lng") double lng
    );

    @GET("stores/direction")
    Call<DirectionModel> getDirections(
            @Query("fromLat") double fromLat,
            @Query("fromLng") double fromLng,
            @Query("toLat") double toLat,
            @Query("toLng") double toLng
    );

    @POST("orders")
    Call<MessModel> postOrder(@Body okhttp3.RequestBody detail);

    @POST("login")
    Call<AuthModel> login(@Body LoginModel request);

    @POST("register")
    Call<AuthModel> register(@Body RegisterModel request);

    @FormUrlEncoded
    @POST("auth/refresh")
    Call<AuthModel> refreshToken(@Field("refresh_token") String refreshToken);

    @POST("chatbot")
    Call<Map<String, String>> sendMessage(@Body Map<String, String> body);

    @POST("logout")
    Call<AuthModel> logout();


    @POST("changepassword")
    Call<ResponseModel> changePassword(
            @Body ChangePassModel body
    );
    @FormUrlEncoded
    @POST("users/forgot-password-send-otp")
    Call<ResponseModel> forgotPasswordSendOtp(@Field("email") String email);
    @GET("getOrder/{userId}")
    Call<List<OrderModel>> getAllOrder(@Path("userId") int userId);

    @PUT("getOrder/cancel/{orderId}")
    Call<MessModel> cancelOrder(@Path("orderId") int orderId);
    @GET("getOrder/detail/{orderId}")
    Call<OrderDetailModel> getOrderDetail(@Path("orderId") int orderId);

}