package com.android.network.error;


import android.util.Log;

import com.android.network.NetworkData;
import com.android.network.utils.NetworkStatus;
import com.google.gson.JsonSyntaxException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * 网络请求异常处理：
 * <p>
 * 1、处理网络层异常，自定义异常
 * 2、处理通过网络层抛出的业务层异常
 */
public class ErrorHandler {

    public static NetworkData handlerError(Throwable throwable) {
        Log.d("http", "handlerError: 处理网络异常");
        NetworkData errorData = new NetworkData();
        errorData.setData(throwable.getMessage());
        if (throwable instanceof HttpException) { // 404、500 网络错误
            errorData.setCode(NetworkStatus.NETWORK_SERVER_EXCEPTION.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_SERVER_EXCEPTION.getErrorMessage());
            try {
                // 业务层异常通过网络层抛出时，特殊处理
                HttpException httpEx = (HttpException) throwable;
                Response response = httpEx.response();
                ResponseBody responseBody = response.errorBody();
                if (responseBody != null) {
                    String result = new String(responseBody.bytes());
                    errorData.setData(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (throwable instanceof TimeoutException
                || throwable instanceof SocketTimeoutException) {
            errorData.setCode(NetworkStatus.NETWORK_SERVER_TIMEOUT.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_SERVER_TIMEOUT.getErrorMessage());
        } else if (throwable instanceof JsonSyntaxException) {
            errorData.setCode(NetworkStatus.NETWORK_JSON_EXCEPTION.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_JSON_EXCEPTION.getErrorMessage());
        } else if (throwable instanceof ErrorException) { // 自定义异常
            errorData.setCode(((ErrorException) throwable).getCode());
            errorData.setMsg(throwable.getMessage());
            errorData.setData("");
        } else {
            errorData.setCode(NetworkStatus.NETWORK_SERVER_EXCEPTION.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_SERVER_EXCEPTION.getErrorMessage());
            throwable.printStackTrace();
        }
        return errorData;
    }
}
