package com.andriod.library.network;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApi {

    @FormUrlEncoded
    @POST("user/login")
    Call<User> login(@Field("phone") String phone, @Field("password") String password);

    @GET("trc_bjcg/u/m/myAccount/accountSummary")
    Call<AccountSummary> accountSummary();

    @GET("trc_bjcg/loans/list")
    Call<FinanceListInfo> financeList(@Query("pageIndex") int pageIndex,
                                      @Query("pageSize") int pageSize,
                                      @Query("displayTerminal") String displayTerminal);

}

