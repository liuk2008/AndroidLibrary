package com.example.android.netcore.http.request;

import android.util.Log;

import com.example.android.netcore.http.engine.HttpEngine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.Map;


// POST请求网络层
public class HttpPostRequest {

    private final static String TAG = "HttpPostRequest";
    private static HttpPostRequest instance = null;
    private HttpEngine engine;

    private HttpPostRequest() {
        engine = new HttpEngine.Builder()
                .setRequestMethod("POST")
                .setContentType("application/json")
                .builder();
    }

    public synchronized static HttpPostRequest getInstance() {
        if (instance == null) {
            instance = new HttpPostRequest();
        }
        return instance;
    }

    /**
     * POST 请求
     * 提交表单数据需要编码
     * 提交json数据不需要编码
     *
     * @param urlStr 请求路径
     * @return 返回的数据
     */
    public String doPost(String urlStr, Map<String, Object> hashMap) {
        Log.d(TAG, "doPost : urlStr = [" + urlStr + "]");
        String params = "";
        if (hashMap != null) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            params = gson.toJson(hashMap);
        }
        Log.d(TAG, "addParameter = [" + params + "]");
        return engine.sendHttpRequest(urlStr, params);
    }

}