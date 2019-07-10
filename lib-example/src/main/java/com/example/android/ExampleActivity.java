package com.example.android;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.android.mvp.BasePresenter;
import com.example.android.mvp.BaseResult;
import com.example.android.mvp.entity.UserInfo;
import com.example.android.mvp.presenter.LoginPresenterImpl;
import com.example.android.netcore.http.engine.HttpsEngine;


public class ExampleActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "ExampleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn1) {
            testMvp();
        } else if (id == R.id.btn2) {
            testHttps();
        }
    }

    private void testMvp() {
        // 获取登录信息
        BasePresenter.LoginPresenter loginPresenter = new LoginPresenterImpl(getRequestManager(), new BaseResult.LoginResult() {
            @Override
            public void onLoginSuccess(UserInfo userInfo) {
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "userInfo:" + userInfo);
            }

            @Override
            public void onLoginFail(int resultCode, String errorMsg) {
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "resultCode:" + resultCode + " message:" + errorMsg);
            }
        });
        loginPresenter.login();
    }


    private void testHttps() {
        final HttpsEngine httpsEngine = new HttpsEngine.Builder().builder(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = httpsEngine.sendHttpRequest("https://www.12306.cn/mormhweb/", null);
                Log.d(TAG, "result: " + result);
            }
        }).start();
    }

}
