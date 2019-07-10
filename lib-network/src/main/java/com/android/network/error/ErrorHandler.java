package com.android.network.error;


import com.android.network.network.NetworkStatus;
import com.google.gson.JsonSyntaxException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * 网络请求异常处理：
 * <p>
 * 1、处理网络层异常时
 * 2、处理通过网络层抛出的业务层异常
 * 3、处理业务层异常
 */
public class ErrorHandler {

    public static ErrorData handlerError(Throwable throwable) {
        ErrorData errorData = new ErrorData();
        errorData.setData(throwable.getMessage());
        if (throwable instanceof TimeoutException
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
        } else if (throwable instanceof HttpException) { // 404、500 网络错误
            errorData.setCode(NetworkStatus.NETWORK_HTTP_EXCEPTION.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_HTTP_EXCEPTION.getErrorMessage());
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
        } else {
            errorData.setCode(NetworkStatus.NETWORK_SERVER_EXCEPTION.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_SERVER_EXCEPTION.getErrorMessage());
            throwable.printStackTrace();
        }
        return errorData;
    }
}
