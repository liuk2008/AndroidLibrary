package com.example.android.mvp.model;

import android.os.AsyncTask;

import com.example.android.netcore.callback.NetCallback;
import com.example.android.netcore.http.task.MyAsyncGetTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 网络层控制器：需考虑退出页面时取消网络请求
 * Created by liuk on 2018/4/4 0004.
 */

public class HttpRequest {

    private static final String TAG = HttpRequest.class.getSimpleName();
    private static HttpRequest httpRequest;
    private Executor executor;

    private HttpRequest() {
        executor = Executors.newCachedThreadPool();
    }

    public static HttpRequest getInstance() {
        if (httpRequest == null) {
            httpRequest = new HttpRequest();
        }
        return httpRequest;
    }

    public AsyncTask login(NetCallback callback) {
        // 创建参数
        String url = "http://jfx.qdfaw.com:8081/api/qingqi/product/login";
        Map<String, Object> params = new HashMap<>();
        params.put("userName", "linqi");
        params.put("password", "Qweasd");
        params.put("deviceId", "1111");
        // 创建接口请求
        MyAsyncGetTask task = new MyAsyncGetTask(url, params, callback);
        // 发送请求
        task.executeOnExecutor(executor);
        return task;
    }

    // 取消请求
    public void cancel(AsyncTask task) {
        if (task != null) {
            task.cancel(true);
        }
    }

}
