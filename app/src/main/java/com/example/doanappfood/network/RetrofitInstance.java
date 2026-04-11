package com.example.doanappfood.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private  static Retrofit retrofit;
    public  static  Retrofit getRetrofit(){
        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("http://192.168.28.1:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
