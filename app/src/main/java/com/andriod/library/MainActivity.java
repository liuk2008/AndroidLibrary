package com.andriod.library;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.andriod.library.database.DatabaseDao;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        // WebView使用demo
//        WebViewHelper webViewHelper = WebViewHelper.create(this);
//        setContentView(webViewHelper.getRootView());
//        webViewHelper.initClient();
//        CookieUtil.setCookie(".lawcert.com", "token", "123");
//        WebViewUtils.setCookie("platform", "finance");
//        WebViewUtils.setCookie("channel", "official");
//        WebViewUtils.setHeader("version", "1.3.0.0");
//        webViewHelper.load("https://jrhelp.lawcert.com/trc_app/disclosure/about");

        // database使用demo
//        DatabaseDao dao = DatabaseDao.getInstance();
//        dao.init(getApplicationContext());
//        dao.testMsgInfo();
//        dao.testUserInfo();
    }
}
