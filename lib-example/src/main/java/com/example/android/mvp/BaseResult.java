package com.example.android.mvp;

import com.example.android.mvp.entity.UserInfo;

/**
 * 业务层回调方法
 * Created by Administrator on 2018/2/1.
 */

public interface BaseResult {

    interface LoginResult {
        void onLoginSuccess(UserInfo userInfo);

        void onLoginFail(int resultCode, String errorMsg);
    }

}
