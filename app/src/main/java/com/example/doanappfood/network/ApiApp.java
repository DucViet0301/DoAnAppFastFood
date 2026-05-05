package com.example.doanappfood.network;

import com.example.doanappfood.model.BannerModel;
import com.example.doanappfood.model.CategoryModel;
import com.example.doanappfood.model.ComboModel;
import com.example.doanappfood.model.DirectionModel;
import com.example.doanappfood.model.NewModel;
import com.example.doanappfood.model.ProductDetailModel;
import com.example.doanappfood.model.ProductModel;
import com.example.doanappfood.model.PromotionNewsModel;
import com.example.doanappfood.model.StoreModel;
import com.example.doanappfood.model.LoginRequest;
import com.example.doanappfood.model.ResponseModel;
import com.example.doanappfood.model.UserModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @POST("users/register")
    Call<ResponseModel> registerUser(@Body UserModel user);

    @POST("users/login")
    Call<ResponseModel> loginUser(@Body LoginRequest loginRequest);


    // THÊM 2 API MỚI CHO TÍNH NĂNG ĐỔI MẬT KHẨU

    @FormUrlEncoded
    @POST("users/send-otp")
    Call<ResponseModel> sendOtp(@Field("email") String email);

    @FormUrlEncoded
    @POST("users/change-password")
    Call<ResponseModel> changePassword(
            @Field("email") String email,
            @Field("otp") String otp,
            @Field("newPassword") String newPassword
    );
    @FormUrlEncoded
    @POST("users/forgot-password-send-otp")
    Call<ResponseModel> forgotPasswordSendOtp(@Field("email") String email);
}