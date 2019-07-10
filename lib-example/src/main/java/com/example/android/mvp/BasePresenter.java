package com.example.android.mvp;

/**
 * Presenter基类
 * 注意：需考虑页面销毁时停止网络层回调
 * Created by Administrator on 2018/2/1.
 */

public interface BasePresenter {

    interface LoginPresenter extends BasePresenter {
        void login();
    }

    void onDestroy();

}
