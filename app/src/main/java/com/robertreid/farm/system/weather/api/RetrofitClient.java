package com.robertreid.farm.system.weather.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //we use this static URL that does not change
    public static final String BASE_URL = "http://api.openweathermap.org/";

    private static RetrofitClient instance = null;
    public Retrofit retrofit;


    // we initialise our retrofit instance in constructor of class.
    public RetrofitClient() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }

        return instance;
    }
}
