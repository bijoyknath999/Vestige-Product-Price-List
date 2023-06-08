package com.vestige.productpricelist.api;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiInterface {

    public static Retrofit retrofit = null, retrofit2 = null;


    public static ApiRequest getApiRequestInterface() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiRequest.class);
    }
}