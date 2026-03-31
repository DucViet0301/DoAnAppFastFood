package com.example.doanappfood.Utlis;

import com.example.doanappfood.model.BannerModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiApp {

    @GET("banners")
    Call<List<BannerModel>> getBanner();
}
