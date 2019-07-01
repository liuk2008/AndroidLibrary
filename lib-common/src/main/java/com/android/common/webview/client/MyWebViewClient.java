package com.android.common.webview.client;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.common.R;
import com.android.common.webview.MyWebView;

import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 处理网络错误，加载自定义页面
 */
public class MyWebViewClient extends WebViewClient {

    private static final String TAG = "MyWebViewClient";
    private WebViewClientInterface webViewClientInterface;
    private ProgressBar progressBar;
    private View errorView;
    private ImageView ivError;
    private TextView tvError, tvTitle;
    private MyWebView myWebView;
    private boolean isFail = false, isNet = false;

    public MyWebViewClient(View view) {
        progressBar = view.findViewById(R.id.progressbar);
        errorView = view.findViewById(R.id.view_error);
        ivError = view.findViewById(R.id.iv_error);
        tvError = view.findViewById(R.id.tv_error);
        tvTitle = view.findViewById(R.id.tv_title);
        myWebView = view.findViewById(R.id.webview);
    }

    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        super.onPageStarted(webView, url, favicon);
        Log.d(TAG, "onPageStarted: ");
        isFail = false;
        isNet = false;
        tvTitle.setText("正在加载中，请稍后");
        progressBar.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
        myWebView.setVisibility(View.GONE);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        Log.d(TAG, "onPageFinished: ");
        progressBar.setVisibility(View.GONE);
        // 显示错误界面
        if (isNet) {
            showError("页面打开失败，请检查网络后再重试", R.drawable.common_net_failed);
        } else if (isFail) {
            tvTitle.setText("网页无法打开");
            showError("页面打开失败，请检查地址后再重试", R.drawable.common_loading_failed);
        } else {
            myWebView.setVisibility(View.VISIBLE);
        }
        // 执行js脚本，JS代码调用一定要在 onPageFinished（） 回调之后才能调用，否则不会调用。
        if (webViewClientInterface != null)
            webViewClientInterface.executorJs(webView, url);
    }

    /**
     * 6.0 以下调用，会在新版本中也可能被调用
     * 加载错误的时候会回调，断网或者超时判断
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 防止重复显示
            return;
        }
        if (errorCode == WebViewClient.ERROR_HOST_LOOKUP
                || errorCode == WebViewClient.ERROR_CONNECT
                || errorCode == WebViewClient.ERROR_IO
                || errorCode == WebViewClient.ERROR_TIMEOUT) {
            isNet = true;
        } else {
            isFail = true;
        }
    }

    /**
     * 6.0 以上调用
     */
    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.d(TAG, "onReceivedError: ");
        if (request.isForMainFrame()) { // 获取当前的网络请求是否是为main frame创建的.
            int errorCode = error.getErrorCode();
            if (errorCode == WebViewClient.ERROR_HOST_LOOKUP
                    || errorCode == WebViewClient.ERROR_CONNECT
                    || errorCode == WebViewClient.ERROR_IO
                    || errorCode == WebViewClient.ERROR_TIMEOUT) {
                isNet = true;
            } else {
                isFail = true;
            }
        }
    }

    /**
     * 404、500 等网络错误会回调这里
     * <p>
     * WebView在请求加载一个页面的同时，还会发送一个请求图标文件的请求。
     * webView.loadUrl("http://192.168.5.40:9006/sso_web/html/H5/doctor/aboutUs.html");
     * 同时还会发送一个请求图标文件的请求
     * http://192.168.5.40:9006/favicon.ico
     * onReceivedHttpError这个方法主要用于响应服务器返回的Http错误(状态码大于等于400)，
     * 这个回调将被调用任何资源（IFRAME，图像等），而不仅仅是主页面。所以就会出现主页面虽然加载成功，
     * 但由于网站没有favicon.ico文件导致返回404错误。
     * 1、在onReceivedHttpError()做相应处理，忽略请求favicon.ico文件404的错误
     * 2、重写WebViewClient的shouldInterceptRequest方法禁用favicon.ico请求
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        Log.d(TAG, "onReceivedHttpError: ");
        if (request.isForMainFrame() && request.getUrl().toString().equals(view.getUrl())) {
            isFail = true;
        }
//        int statusCode = errorResponse.getStatusCode();
//        Log.d(TAG, "onReceivedHttpError: " + statusCode);
//        Log.d(TAG, "onReceivedHttpError: " + request.getUrl());
//        Log.d(TAG, "onReceivedHttpError: " + view.getUrl());
//        Log.d(TAG, "onReceivedHttpError: " + request.isForMainFrame());

//        if (request.isForMainFrame() && !request.getUrl().getPath().endsWith("/favicon.ico")) {
//            showError("页面打开失败，请检查地址后再重试", R.drawable.common_loading_failed);
//        }
    }

    /**
     * 当接收到https错误时，会回调此函数，在其中可以做错误处理
     * 在重写WebViewClient的onReceivedSslError方法时，注意一定要去除onReceivedSslError方法的super.onReceivedSslError(view, handler, error);，否则设置无效。
     */
    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler handler, SslError error) {
        //super.onReceivedSslError(webView, sslErrorHandler, sslError);
        Log.d(TAG, "onReceivedSslError: ");
        // 忽略SSL证书错误，继续加载页面
        handler.proceed();
        if (webViewClientInterface != null)
            webViewClientInterface.SSLException(webView, handler, error);
    }


    /**
     * 拦截 url 跳转,在里边添加点击链接跳转或者操作
     * 1、WebView的前进、后退、刷新、以及post请求都不会调用shouldOverrideUrlLoading方法
     * 2、若设置 WebViewClient 且该方法返回 true ，则说明由应用的代码处理该 url，WebView 不处理，也就是程序员自己做处理。
     * 3、若设置 WebViewClient 且该方法返回 false，则说明由 WebView 处理该 url，即用 WebView 加载该 url。
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        Log.d(TAG, "shouldOverrideUrlLoading: url-->" + url);
        boolean result = false;
        if (webViewClientInterface != null)
            result = webViewClientInterface.handleUrl(url);  // 处理特定url
        if (!result)
            webView.loadUrl(url);
        return true;
    }

    /**
     * 显示自定义错误页
     */
    private void showError(String error, int src) {
        errorView.setVisibility(View.VISIBLE);
        tvError.setText(error);
        ivError.setBackgroundResource(src);
    }

    public interface WebViewClientInterface {
        // 处理特定url
        boolean handleUrl(String url);

        // 执行js脚本
        void executorJs(WebView webView, String url);

        // 处理https错误
        void SSLException(WebView webView, SslErrorHandler handler, SslError error);
    }

    public void setWebViewClientInterface(WebViewClientInterface webViewClientInterface) {
        this.webViewClientInterface = webViewClientInterface;
    }

    @Nullable
    @Override
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        executeRequest(request);
        return super.shouldInterceptRequest(view, request);
    }

    /**
     * 需单独建立链接从header里面取token
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private void executeRequest(final WebResourceRequest request) {
        final String url = request.getUrl().toString();
        String scheme = request.getUrl().getScheme();
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.connect();
                        int responseCode = connection.getResponseCode();
                        if (responseCode == 200) {
                            String cookie = connection.getHeaderField("Set-Cookie");
                            Log.d(TAG, "cookie: " + cookie);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    }

}
