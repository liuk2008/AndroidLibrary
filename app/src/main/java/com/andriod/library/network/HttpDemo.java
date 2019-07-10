package com.andriod.library.network;

import android.util.Log;

import com.andriod.library.network.model.AccountSummary;
import com.andriod.library.network.model.User;
import com.android.network.callback.Callback;
import com.android.network.http.request.HttpParams;
import com.android.network.http.request.HttpUtils;

/**
 * Created by LiuK on 2019/7/6
 */
public class HttpDemo {

    public static void userInfo() {
        HttpParams httpParams = new HttpParams.Builder()
                .setUrl("https://passport.lawcert.com/proxy/account/user/login")
                .appendParams("phone", "18909131172")
                .appendParams("password", "123qwe")
                .builder();
        HttpUtils.doPost(httpParams, new Callback<User>() {
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
        HttpParams httpParams = new HttpParams.Builder()
                .setUrl("https://www.lawcert.com/proxy/trc_bjcg/u/m/myAccount/accountSummary")
                .builder();
        HttpUtils.doGet(httpParams, new Callback<AccountSummary>() {
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
