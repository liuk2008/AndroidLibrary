package com.example.android.mvp.model.callback;

import android.util.Log;

import com.example.android.mvp.model.ApiResponse;
import com.example.android.netcore.callback.NetCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * 中间层，关联网络层与业务层回调方法
 * Created by liuk on 2018/7/9 0009.
 */

public class NetCallAdapter<T> implements NetCallback {

    private static final String TAG = NetCallAdapter.class.getSimpleName();
    private ResponseCallback responseCallback;
    private Type type;

    public NetCallAdapter(Type type, ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
        this.type = type;
    }

    // 网络层回调方法
    @Override
    public void netSuccess(String result) {
        Log.d(TAG, "netSuccess: " + result);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        // 业务层回调方法
        try {
            ApiResponse<T> apiResponse = gson.fromJson(result, type);
            int resultCode = apiResponse.getResultCode();
            switch (resultCode) {
                case 200:
                    responseCallback.onSuccess(apiResponse.getData());
                    break;
                case 509:
                    responseCallback.onCookieExpired();
                    responseCallback.onFail(resultCode, apiResponse.getMessage());
                    break;
                case 0:
                    apiResponse.setMessage("Json转换异常");
                default:
                    responseCallback.onFail(resultCode, apiResponse.getMessage());
                    break;
            }
        } catch (Exception e) {
            responseCallback.onFail(0, "程序异常");
        }
    }

    @Override
    public void netFail(int resultCode, String msg) {
        responseCallback.onFail(resultCode, msg);
    }
}
