package com.example.doanappfood.network;

import com.example.doanappfood.model.BannerModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiApp {

    @GET("banners")
    Call<List<BannerModel>> getBanner();
}
