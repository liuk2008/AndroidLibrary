package com.andriod.library.network;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitApi {

    @FormUrlEncoded
    @POST("user/login")
    Call<User> login(@Field("phone") String phone, @Field("password") String password);

    @GET("trc_bjcg/u/m/myAccount/accountSummary")
    Call<AccountSummary> accountSummary();
}

