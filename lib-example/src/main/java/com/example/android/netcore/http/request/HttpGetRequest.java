package com.example.android.netcore.http.request;

import android.text.TextUtils;
import android.util.Log;


import com.example.android.netcore.http.engine.HttpEngine;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

// GET请求网络层
public class HttpGetRequest {

    private final static String TAG = "HttpGetRequest";
    private static HttpGetRequest instance = null;
    private HttpEngine engine;

    private HttpGetRequest() {
        engine = new HttpEngine.Builder().builder();
    }

    public synchronized static HttpGetRequest getInstance() {
        if (instance == null) {
            instance = new HttpGetRequest();
        }
        return instance;
    }

    /**
     * GET 请求
     *
     * @param urlStr 请求路径，请求参数赋值在url上
     * @return 返回的数据
     */
    public String doGet(String urlStr, Map<String, Object> hashMap) {
        Log.d(TAG, "doGet : urlStr = [" + urlStr + "]");
        // 将请求参数进行编码
        String params = addParameter(hashMap);
        if (!TextUtils.isEmpty(params)) {
            urlStr = urlStr + "?" + params;
        }
        Log.d(TAG, "doGet : newUrl = [" + urlStr + "]");
        return engine.sendHttpRequest(urlStr, "");
    }

    // 拼接参数列表
    private String addParameter(Map<String, Object> paramsMap) {
        String paramStr = "";
        if (paramsMap != null && paramsMap.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String key : paramsMap.keySet()) {
                stringBuilder.append(key);
                stringBuilder.append("=");
                try {
                    String value = String.valueOf(paramsMap.get(key));
                    stringBuilder.append(URLEncoder.encode(value, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return "";
                }
                stringBuilder.append("&");
            }
            paramStr = stringBuilder.substring(0, stringBuilder.length() - 1);
        }
        return paramStr;
    }
}