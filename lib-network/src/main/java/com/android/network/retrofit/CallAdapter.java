package com.android.network.retrofit;

import android.util.Log;

import com.android.network.callback.Callback;
import com.android.network.error.ErrorData;
import com.android.network.error.ErrorHandler;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by liuk on 2018/3/29 0029.
 */
public class CallAdapter<T> implements retrofit2.Callback<T> {

    private static final String TAG = "CallAdapter";
    private Callback<T> mCallback;
    private Call<T> mCall;

    public CallAdapter(Call<T> call, Callback<T> callback) {
        mCall = call;
        mCallback = callback;
    }

    // retrofit 响应结果执行在UI线程，不过OkHttp的onResponse结果执行在子线程中
    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()) { // 网络层200
            try {
                T t = response.body(); // 注意 response 不能被解析的情况下，response.body()会返回null
                if (mCallback != null) mCallback.onSuccess(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {     // 404，500 时执行
            ResponseBody errorBody = response.errorBody();
            String result = null;
            try {
                result = new String(errorBody.bytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mCallback != null)
                mCallback.onFail(response.code(), "服务器访问异常", result);
        }
    }

    //  当一个请求取消时，回调方法onFailure()会执行，而onFailure()方法在没有网络或网络超时的时候也会执行。
    @Override
    public void onFailure(Call<T> call, Throwable throwable) {
        throwable.printStackTrace();
        if (!call.isCanceled() && mCallback != null) {
            ErrorData errorData = ErrorHandler.handlerError(throwable);
            mCallback.onFail(errorData.getCode(), errorData.getMsg(), errorData.getData());
        } else {
            Log.d(TAG, "onFailure: 取消网络请求");
        }
    }

    public void cancel() {
        if (mCall != null && !mCall.isCanceled())
            mCall.cancel();
        mCallback = null;
    }

}
