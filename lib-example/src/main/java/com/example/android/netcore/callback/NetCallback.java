package com.example.android.netcore.callback;


/**
 * 网络层回调方法
 * Created by Administrator on 2018/2/1.
 */

public interface NetCallback {

    /**
     *  网络层200情况下，回调
     * @param result
     */
    void netSuccess(String result);

    /**
     * 网络层非200情况下，回调
     * @param resultCode
     * @param msg
     */
    void netFail(int resultCode, String msg);
}
