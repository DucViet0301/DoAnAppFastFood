package com.example.doanappfood.Utlis;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private  static Retrofit retrofit;
    public  static  Retrofit getRetrofit(){
        if(retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:3000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
