package com.vestige.productpricelist.api;


import com.vestige.productpricelist.models.Category;
import com.vestige.productpricelist.models.InstallUsers;
import com.vestige.productpricelist.models.Policy;
import com.vestige.productpricelist.models.Privacy;
import com.vestige.productpricelist.models.Product;
import com.vestige.productpricelist.models.Settings;
import com.vestige.productpricelist.models.Sliders;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiRequest {
    @FormUrlEncoded
    @POST("Settings/users")
    Call<List<Settings>> getSettings(@Field("apikey") String apikey);

    @FormUrlEncoded
    @POST("Installupdate/users")
    Call<InstallUsers> saveInstallation(@Field("apikey") String apikey);

    @FormUrlEncoded
    @POST("Slids/users")
    Call<List<Sliders>> getSliders(@Field("apikey") String apikey);

    @FormUrlEncoded
    @POST("Category/users")
    Call<List<Category>> getCategory(@Field("apikey") String apikey,
                                     @Field("offset") String offset);

    @FormUrlEncoded
    @POST("Policy/users")
    Call<Policy> getPolicy(@Field("apikey") String apikey);

    @FormUrlEncoded
    @POST("Vestigepricelist/users")
    Call<List<Product>> getProductsByCat(@Field("apikey") String apikey,
                                         @Field("offset") String offset,
                                         @Field("catid") String catid);

    @FormUrlEncoded
    @POST("Productquery/users")
    Call<List<Product>> getProductsByID(@Field("apikey") String apikey,
                                  @Field("mid") String mid);

    @FormUrlEncoded
    @POST("Policy/users")
    Call<List<Privacy>> getAllText(@Field("apikey") String apikey);
}
