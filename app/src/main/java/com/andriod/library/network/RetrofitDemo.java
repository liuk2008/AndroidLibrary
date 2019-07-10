package com.andriod.library.network;


import android.util.Log;

import com.andriod.library.network.model.AccountSummary;
import com.andriod.library.network.model.User;
import com.android.network.callback.Callback;
import com.android.network.network.NetworkStatus;
import com.android.network.retrofit.RetrofitEngine;
import com.android.network.retrofit.RetrofitUtils;

import retrofit2.Call;

public class RetrofitDemo {

    private static RetrofitEngine retrofitEngine = RetrofitEngine.getInstance();

    public static void userInfo() {
        RetrofitApi accountApi = retrofitEngine.getRetrofitService(RetrofitApi.class,
                "https://passport.lawcert.com/proxy/account/",
                NetworkStatus.Type.RETROFIT_DEFAULT);
        Call<User> call = accountApi.login("18909131172", "123qwe");
        RetrofitUtils.request(call, new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("http", "onSuccess: " + user);
                accountSummary();
            }

            @Override
            public void onFail(int resultCode, String msg, String data) {

            }
        });
    }

    public static void accountSummary() {
        RetrofitApi financeApi = retrofitEngine.getRetrofitService(RetrofitApi.class,
                "https://www.lawcert.com/proxy/",
                NetworkStatus.Type.RETROFIT_DEFAULT_DATAWRAPPER);
        Call<AccountSummary> call = financeApi.accountSummary();
        RetrofitUtils.request(call, new Callback<AccountSummary>() {
            @Override
            public void onSuccess(AccountSummary accountSummary) {
                Log.d("http", "onSuccess: " + accountSummary);
            }

            @Override
            public void onFail(int resultCode, String msg, String data) {

            }
        });
    }


}
