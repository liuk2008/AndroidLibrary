package com.andriod.library.network;


import android.util.Log;

import com.android.network.callback.Callback;
import com.android.network.network.NetworkStatus;
import com.android.network.retrofit.CallAdapter;
import com.android.network.retrofit.RetrofitEngine;
import com.android.network.retrofit.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RetrofitDemo {

    private static RetrofitEngine retrofitEngine = RetrofitEngine.getInstance();
    private List<CallAdapter> callList = new ArrayList<>();

    public void userInfo() {
        RetrofitApi accountApi = retrofitEngine.getRetrofitService(RetrofitApi.class,
                "https://passport.lawcert.com/proxy/account/",
                NetworkStatus.Type.RETROFIT_DEFAULT);
        Call<User> call = accountApi.login("18909131172", "123qwe");
        CallAdapter callAdapter = RetrofitUtils.request(call, new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("http", "onSuccess: " + user);
//                accountSummary();
            }

            @Override
            public void onFail(int resultCode, String msg, String data) {

            }
        });
        callList.add(callAdapter);
    }

    public void accountSummary() {
        RetrofitApi financeApi = retrofitEngine.getRetrofitService(RetrofitApi.class,
                "https://www.lawcert.com/proxy/",
                NetworkStatus.Type.RETROFIT_DEFAULT_DATAWRAPPER);
        Call<AccountSummary> call = financeApi.accountSummary();
        CallAdapter callAdapter = RetrofitUtils.request(call, new Callback<AccountSummary>() {
            @Override
            public void onSuccess(AccountSummary accountSummary) {
                Log.d("http", "onSuccess: " + accountSummary);
            }

            @Override
            public void onFail(int resultCode, String msg, String data) {

            }
        });
        callList.add(callAdapter);
    }

    public void financeList(int pageIndex, Callback<FinanceListInfo> callback) {
        RetrofitApi financeApi = retrofitEngine.getRetrofitService(RetrofitApi.class,
                "https://www.lawcert.com/proxy/",
                NetworkStatus.Type.RETROFIT_DEFAULT_DATAWRAPPER);

        Call<FinanceListInfo> call = financeApi.financeList(pageIndex, 40, "app");
        RetrofitUtils.request(call, callback);
    }


    public void cancelTask(CallAdapter callAdapter) {
        callList.remove(callAdapter);
        RetrofitUtils.cancelTask(callAdapter);
    }

    public void cancelAll() {
        RetrofitUtils.cancelAll(callList);
    }


}
