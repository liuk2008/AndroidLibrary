package com.android.network.callback;


public interface Callback<T> {

    void onSuccess(T t);

    void onFail(int resultCode, String msg, String data);

}
