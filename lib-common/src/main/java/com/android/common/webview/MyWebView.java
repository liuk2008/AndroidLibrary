package com.android.common.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.common.webview.client.WebViewUtils;

import java.util.HashMap;
import java.util.Map;

public class MyWebView extends WebView {

    private static final String TAG = MyWebView.class.getSimpleName();

    public MyWebView(Context context) {
        super(context);
        initSetting();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSetting();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSetting();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initSetting() {
        // 禁止Android系统js注入
        removeJavascriptInterface("accessibility");
        removeJavascriptInterface("accessibilityTraversal");
        removeJavascriptInterface("searchBoxJavaBridge_");

        WebView.setWebContentsDebuggingEnabled(true); // 设置WebView可调试
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        //缩放操作
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true);   //将图片调整到合适的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //允许Https页面内打开Http链接
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowUniversalAccessFromFileURLs(true); // 解决跨域的问题，访问其他网站接口。
        }

    }

    @Override
    public void loadUrl(String url) {
        setCookie(url);
        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        setCookie(url);
        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void reload() {
        String url = getUrl();
        setCookie(url);
        super.reload();
    }

    /**
     * 设置cookie
     */
    private void setCookie(String url) {
        if (TextUtils.isEmpty(url)) return;
        if (WebViewUtils.getCookieMap().size() <= 0) return;
        if (url.startsWith("http")) {
            Uri uri = Uri.parse(url);
            String domain = uri.getHost();
            Map<String, String> hashMap = new HashMap<>(WebViewUtils.getCookieMap());
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            for (String key : hashMap.keySet()) {
                String value = hashMap.get(key);
                cookieManager.setCookie(domain, key + "=" + value);
            }
            // 只有cookie的domain和path与请求的URL匹配才会发送这个cookie。
            cookieManager.setCookie(domain, "domain=" + domain);
            cookieManager.setCookie(domain, "path=/");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cookieManager.flush(); // 立即同步cookie的操作
            }
        }
    }

}
