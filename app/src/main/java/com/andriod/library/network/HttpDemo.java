package com.andriod.library.network;

import android.util.Log;

import com.android.network.callback.Callback;
import com.android.network.http.request.HttpParams;
import com.android.network.http.request.HttpTask;
import com.android.network.http.request.HttpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuK on 2019/7/6
 */
public class HttpDemo {

    private static List<HttpTask> taskList = new ArrayList<>();

    public static void userInfo() {
        HttpParams httpParams = new HttpParams.Builder()
                .setUrl("https://passport.lawcert.com/proxy/account/user/login")
                .appendParams("phone", "18909131172")
                .appendParams("password", "123qwe")
                .builder();
        HttpTask httpTask = HttpUtils.doPost(httpParams, new Callback<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d("http", "onSuccess: " + user);
                accountSummary();
            }

            @Override
            public void onFail(int resultCode, String msg, String data) {
                Log.d("http", "onFail: resultCode:" + resultCode + ", msg:" + msg + ", data:" + data);

            }
        });
        taskList.add(httpTask);
    }

    public static void accountSummary() {
        HttpParams httpParams = new HttpParams.Builder()
                .setUrl("https://www.lawcert.com/proxy/trc_bjcg/u/m/myAccount/accountSummary")
                .builder();
        HttpTask httpTask = HttpUtils.doGet(httpParams, new Callback<AccountSummary>() {
            @Override
            public void onSuccess(AccountSummary accountSummary) {
                Log.d("http", "onSuccess: " + accountSummary);

            }

            @Override
            public void onFail(int resultCode, String msg, String data) {

            }
        });
        taskList.add(httpTask);
    }

    public void cancelTask(HttpTask task) {
        taskList.remove(task);
        HttpUtils.cancelTask(task);
    }

    public void cancelAll() {
        HttpUtils.cancelAll(taskList);
    }

}
