package com.example.android.mvp.model.callback;


/**
 * 处理网络层回调
 * 1、将网络层数据封装为对象
 * 2、处理业务层回调方法
 * Created by liuk on 2018/4/4 0004.
 */

public abstract class ResponseCallback<T> {

    public abstract void onSuccess(T t);

    public abstract void onFail(int resultCode, String errorMsg);

    public void onCookieExpired() {
    }
}
