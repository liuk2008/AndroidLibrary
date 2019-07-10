package com.android.network.http.request;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.android.network.ApiResponse;
import com.android.network.Null;
import com.android.network.callback.Callback;
import com.android.network.error.ErrorData;
import com.android.network.error.ErrorHandler;
import com.android.network.http.NetData;
import com.android.network.http.engine.HttpEngine;
import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 异步执行http/https请求任务
 */
public class HttpTask<T> extends AsyncTask<Void, Void, NetData> {

    private static final String TAG = "http";
    private HttpEngine httpEngine;
    private String mRequestMethod;
    private Callback mCallback;
    private Type type, wrapperType;

    public HttpTask(@NonNull HttpParams httpParams, @NonNull Callback<T> callback, String requestMethod) {
        httpEngine = HttpEngine.getInstance();
        httpEngine.setHttpParams(httpParams);
        mCallback = callback;
        mRequestMethod = requestMethod;
    }

    @Override
    protected NetData doInBackground(Void... voids) {
        NetData netData = new NetData();
        try {
            if (!isCancelled() && mCallback != null) {
                // 获得超类的泛型参数的实际类型
                ParameterizedType parameterizedType = (ParameterizedType) mCallback.getClass().getGenericInterfaces()[0];
                type = parameterizedType.getActualTypeArguments()[0];
                wrapperType = getWrapperType();
                if ("GET".equals(mRequestMethod)) {
                    return httpEngine.doGet();
                } else {
                    return httpEngine.doPost();
                }
            }
        } catch (Exception e) {
            /*
             * 网络层非200情况下
             * 1、网络异常时，捕获异常，通过ErrorHandler处理
             * 2、网络正常，业务层异常通过网络层抛出时，通过ErrorHandler处理
             */
            ErrorData errorData = ErrorHandler.handlerError(e);
            netData.setCode(errorData.getCode());
            netData.setMsg(errorData.getMsg());
            netData.setData(errorData.getData());
            e.printStackTrace();
        }
        return netData;
    }

    @Override
    protected void onPostExecute(NetData data) {
        super.onPostExecute(data);
        if (mCallback == null)
            return;
        try {
            if (data.getCode() == 200) { // 网络层200
                Gson gson = new Gson();
                ApiResponse result = gson.fromJson(data.getData(), wrapperType);
                Log.d(TAG, "onPostExecute: " + result);
                if (result == null) {   // 业务层不存在数据，使用Null对象解析
                    mCallback.onSuccess(Null.INSTANCE);
                } else { // 业务层存在数据
                    if (!TextUtils.isEmpty(result.getResultCode())) { // 业务层数据格式标准
                        int code = Integer.valueOf(result.getResultCode());
                        if (200 == code) { // 业务层200
                            if (null == result.getData()) {
                                mCallback.onSuccess(Null.INSTANCE); // 不存在数据，使用Null对象解析
                            } else {
                                mCallback.onSuccess(result.getData());// 存在数据，使用数据model解析
                            }
                        } else { // 业务层非200
                            mCallback.onFail(code, result.getMessage(), "");
                        }
                    } else { // 业务层数据非标准格式，使用数据model解析
                        T t = gson.fromJson(data.getData(), type);
                        mCallback.onSuccess(t);
                    }
                }
            } else {
                mCallback.onFail(data.getCode(), data.getMsg(), data.getData());
            }
        } catch (Exception e) {
            mCallback.onFail(data.getCode(), data.getMsg(), e.getMessage());
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, "cancel: 取消网络请求");
    }

    private Type getWrapperType() {
        Type wrapperType = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                Type[] types = new Type[1];
                types[0] = type;
                return types;
            }

            @Override
            public Type getRawType() {
                return ApiResponse.class;
            }

            @Override
            public Type getOwnerType() {
                return ApiResponse.class;
            }
        };
        return wrapperType;
    }

    /**
     * 取消网络请求
     */
    public void cancel() {
        cancel(true);
        // 由于callback持有Activity/Fragment引用，设置为null防止内存泄漏
        mCallback = null;
    }

}
