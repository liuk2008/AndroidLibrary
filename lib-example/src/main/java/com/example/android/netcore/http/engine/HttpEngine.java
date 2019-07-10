package com.example.android.netcore.http.engine;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.netcore.callback.NetData;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络核心层
 */
public class HttpEngine {

    private final static String TAG = "HttpEngine";
    private final static String ENCODE_TYPE = "utf-8";
    private final static int TIMEOUT_IN_MILLIONS = 15 * 1000;
    private String requestMethod = "GET";
    private String contentType = "application/x-www-form-urlencoded";

    private HttpEngine(Builder builder) {
        if (!TextUtils.isEmpty(builder.requestMethod)) {
            requestMethod = builder.requestMethod;
        }
        if (!TextUtils.isEmpty(builder.contentType)) {
            contentType = builder.contentType;
        }
    }

    public String sendHttpRequest(String urlStr, String params) {
        HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        PrintWriter out = null;
        int responseCode = -1;
        String result = "网络异常";
        try {
            URL realUrl = new URL(urlStr);
            connection = (HttpURLConnection) realUrl.openConnection();
            // 设置连接主机超时
            connection.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            // 设置从主机读取数据超时
            connection.setReadTimeout(TIMEOUT_IN_MILLIONS);
            // 设置请求方式
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Charset", ENCODE_TYPE);
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Response-Type", "json");
//            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//            connection.setRequestProperty("Content-Type", "application/json");
            // 设置POST方式
            if ("POST".equals(requestMethod.toUpperCase())) {
                // Post请求不能使用缓存
                connection.setUseCaches(false);
                // 发送POST请求必须设置如下两行
                connection.setDoOutput(true);
                connection.setDoInput(true);
                // 设置参数
                if (!TextUtils.isEmpty(params) && !"".equals(params.trim())) {
                    // 获取URLConnection对象对应的输出流
                    out = new PrintWriter(connection.getOutputStream());
                    // 发送请求参数
                    out.print(params);
                    // flush 输出流的缓冲
                    out.flush();
                }
            }
            // 发送请求设置连接，也可以不写
            connection.connect();
            // 获得服务器响应的结果和状态码
            responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                is = connection.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                result = baos.toString();
            } else {
                Log.d(TAG, "responseCode is not 200");
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception");
            e.printStackTrace();
        } finally { // 关闭资源
            try {
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (connection != null)
                connection.disconnect();
        }
        return createMsg(responseCode, result);
    }

    private String createMsg(int code, String msg) {
        NetData status = new NetData(code, msg);
        Gson gson = new Gson();
        return gson.toJson(status);
    }

    // Builder模式
    public static class Builder {
        private String requestMethod = "";
        private String contentType = "";

        public Builder setRequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public HttpEngine builder() {
            return new HttpEngine(this);
        }
    }
}