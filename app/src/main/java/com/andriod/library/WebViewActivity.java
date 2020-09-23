package com.andriod.library;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

import com.android.common.utils.common.LogUtils;
import com.android.common.webview.WebViewHelper;
import com.android.common.webview.client.CookieUtil;
import com.android.common.webview.client.MyWebChromeClient;
import com.android.common.webview.client.MyWebViewClient;
import com.android.common.webview.client.WebViewUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = WebViewActivity.class.getSimpleName();
    private WebViewHelper webViewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webViewHelper = WebViewHelper.create(this);
        setContentView(webViewHelper.getRootView());
        webViewHelper.initClient();
        testCookie();
//        testImage();
//        testFile();
    }

    /**
     * 测试 cookie和header
     */
    private void testCookie() {
        CookieUtil.setCookie("lawcert.com", "token", "1234");
        WebViewUtils.setCookie("platform", "finance");
        WebViewUtils.setCookie("channel", "official");
        WebViewUtils.setHeader("version", "1.3.0.0");
        webViewHelper.load("https://jrhelp.lawcert.com/trc_app/disclosure/about");
//        webViewHelper.load("http://www.baidu.com");
//        webViewHelper.load("https://h5.lawcert.com/trade/withdraw");
//        webViewHelper.load("https://jrwx.lawcert.com/index");
    }

    /**
     * 测试js注入功能
     * 增加js文件，注入到h5页面，关联本地方法与js方法，实现点击图片放大、缩小、长按等功能
     */
    private void testImage() {
        webViewHelper.setWebViewClientInterface(new MyWebViewClient.WebViewClientInterface() {
            @Override
            public boolean handleUrl(String url) {
                LogUtils.logd(TAG, "onLoadUrl:" + url);
                return false;
            }

            @Override
            public void executorJs(WebView webView, String url) {
                LogUtils.logd(TAG, "executorJs: ");
            }

            @Override
            public void SSLException(WebView view, SslErrorHandler handler, SslError error) {

            }

        });
        webViewHelper.load("file:///android_asset/test.html");
    }

    /**
     * 测试WebView文件上传功能
     */
    private void testFile() {
        webViewHelper.setWebChromeClientInterface(new MyWebChromeClient.OpenFileChooserCallBack() {
            // ===============点击表单选择图片或者拍照==============
            @Override
            public void showPicWindow() {
                webViewHelper.showPicWindow();
            }
        });
        String url = "http://jfx.qdfaw.com:6180/truck-qingdao-web/";
        String token = "b2f65c72c3904d5c95c0ffc55c9f5a9e";
        try {
            token = URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        url = String.format("%s?token=%s&appType=%s&userType=%s&platform=%s&cityNmae=%s", url,
                token, "2", "1", "2", "西安");
        webViewHelper.load(url);
    }

    // 文件上传必须实现此方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webViewHelper.setPicData(requestCode, resultCode, data);
    }

}
