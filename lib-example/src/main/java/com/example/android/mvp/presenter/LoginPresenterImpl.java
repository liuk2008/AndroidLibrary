package com.example.android.mvp.presenter;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.mvp.BasePresenter;
import com.example.android.mvp.BaseResult;
import com.example.android.mvp.entity.UserInfo;
import com.example.android.mvp.model.ApiResponse;
import com.example.android.mvp.model.HttpRequest;
import com.example.android.mvp.RequestManager;
import com.example.android.mvp.model.callback.NetCallAdapter;
import com.example.android.mvp.model.callback.ResponseCallback;
import com.example.android.netcore.callback.NetCallback;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * 使用 AsyncTask + Callback 进行请求
 * 处理内存泄露问题：
 * 1、中断View层与Presenter层强引用 / 使用弱引用WearReference进行处理
 * 2、页面销毁时统一取消网络回调
 * Created by Administrator on 2018/2/1.
 */

public class LoginPresenterImpl implements BasePresenter.LoginPresenter {

    private static final String TAG = LoginPresenterImpl.class.getSimpleName();
    private HttpRequest httpRequest;
    private BaseResult.LoginResult result;
    private AsyncTask task;
    private RequestManager requestManager;
    //    private WeakReference<BaseResult.LoginResult> reference;

    public LoginPresenterImpl(RequestManager requestManager, BaseResult.LoginResult result) {
        httpRequest = HttpRequest.getInstance();
        this.result = result;
        this.requestManager = requestManager;
//        reference = new WeakReference<>(result);
    }

    // 关联网络层与业务层
    @Override
    public void login() {
        // 创建返回数据类型
        Type typeToken = new TypeToken<ApiResponse<UserInfo>>() {
        }.getType();
        // 关联业务层
        NetCallback callback = new NetCallAdapter(typeToken, new ResponseCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
//                BaseResult.LoginResult result = reference.get();
                if (result == null) {
                    Log.d(TAG, "onSuccess: 页面销毁");
                } else {
                    result.onLoginSuccess(userInfo); // 页面正常时回调
                }
            }

            @Override
            public void onFail(int resultCode, String errorMsg) {
//                BaseResult.LoginResult result = reference.get();
                if (result == null) {
                    Log.d(TAG, "onFail: 页面销毁");
                } else {
                    result.onLoginFail(resultCode, errorMsg);
                }
            }
        });
        // 调用网络层
        task = httpRequest.login(callback);
        requestManager.addPresenter(this);
    }

    @Override
    public void onDestroy() {
        // 取消网络请求
        httpRequest.cancel(task);
        // 中断View层强引用
        result = null;
    }

}
