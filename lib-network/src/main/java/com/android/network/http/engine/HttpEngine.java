package com.android.network.http.engine;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.network.NetworkData;
import com.android.network.header.MyCookie;
import com.android.network.header.MyCookieManager;
import com.android.network.header.MyHeaderManager;
import com.android.network.http.request.HttpParams;
import com.android.network.utils.NetworkStatus;
import com.android.network.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * http网络核心层
 * <p>
 * * 关于要不要显示调用connect方法的问题：
 * * 1、不需要显示调用connect方法
 * * 2、必须调用getResponseCode()方法
 * * 在不调用getResponseCode()方法的时候，无论是否调用connect()方法，请求都是不能成功的，调用connect()方法只是建立连接，并不会向服务器传递数据，
 * * 只用调用getResponseCode()方法时，才会向服务器传递数据(有博文说是getInputStream()才会向服务器传递数据，getResponseCode中会调用getInputStream方法)。
 * * 跟着getResponseCode()源码发现里面调用了getInputStream()方法，在getInputStream()方法中会判断当前是否连接，如果没有连接，则调用connect()方法建立连接。
 */
public class HttpEngine {
    private final static String TAG = "http";
    private final static String ENCODE_TYPE = "utf-8";
    private static HttpEngine httpEngine;
    private Context mContext;
    private HttpParams httpParams;
    private MyCookieManager myCookieManager;
    private MyHeaderManager myHeaderManager;
    private boolean mIsProxy = true;

    public static synchronized HttpEngine getInstance() {
        if (httpEngine == null) {
            httpEngine = new HttpEngine();
        }
        return httpEngine;
    }

    public void init(Context context) {
        mContext = context;
        myCookieManager = MyCookieManager.getInstance();
        myHeaderManager = MyHeaderManager.getInstance();
    }

    public void isProxy(boolean isProxy) {
        mIsProxy = isProxy;
    }


    public void setHttpParams(HttpParams httpParams) {
        this.httpParams = httpParams;
    }

    private NetworkData checkNet() {
        if (null == mContext)
            throw new RuntimeException("未初始化 HttpEngine");
        return NetworkUtils.checkNet(mContext);
    }

    /**
     * GET 请求
     * 请求参数赋值在url
     */
    public NetworkData doGet() throws Exception {
        NetworkData netData = checkNet();
        if (netData != null) return netData;
        String url = httpParams.url;
        String params = addParameter(httpParams.params);
        return request(url, params, "GET");
    }

    /**
     * POST 请求
     * 提交表单数据需要编码
     * 提交json数据不需要编码
     */
    public NetworkData doPost() throws Exception {
        NetworkData netData = checkNet();
        if (netData != null) return netData;
        String url = httpParams.url;
        String params = "";
        if ("application/json".equals(httpParams.contentType)) {
            if (httpParams.params != null) {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                params = gson.toJson(httpParams.params);
            }
        } else {
            params = addParameter(httpParams.params);
        }
        return request(url, params, "POST");
    }

    public NetworkData request(String url, String params, String requestMethod) throws Exception {
        HttpURLConnection connection = null;
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        PrintWriter out = null;
        int responseCode = -1;
        String msg = "服务器访问异常";
        String result = "";
        try {
            String realUrl = url;
            // 设置Get请求参数
            if ("GET".equalsIgnoreCase(requestMethod) && !TextUtils.isEmpty(params)) {
                realUrl = url + "?" + params;
            }
            // 初始化URL
            URL httpUrl = new URL(realUrl);
            // mIsProxy=true：可以使用代理，mIsProxy=false：禁止使用代理
            if (!mIsProxy)
                connection = (HttpURLConnection) httpUrl.openConnection(Proxy.NO_PROXY);
            else
                connection = (HttpURLConnection) httpUrl.openConnection();
            // 设置连接主机超时
            connection.setConnectTimeout(NetworkStatus.Type.TIMEOUT_MILLISECONDS);
            // 设置从主机读取数据超时
            connection.setReadTimeout(NetworkStatus.Type.TIMEOUT_MILLISECONDS);
            // 设置请求方式
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Charset", ENCODE_TYPE);
            connection.setRequestProperty("Connection", "keep-alive");
            if (!TextUtils.isEmpty(httpParams.contentType)) {
                connection.setRequestProperty("Content-Type", httpParams.contentType);
            } else {
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            }
            connection.setRequestProperty("Response-Type", "json");
            // 设置自定义请求头
            Map<String, String> headers = myHeaderManager.getHeader();
            if (headers.size() > 0) {
                for (String header : headers.keySet()) {
                    connection.setRequestProperty(header, headers.get(header));
                }
            }
            // 设置cookie
            StringBuilder cookieBuilder = new StringBuilder();
            List<MyCookie> myCookies = myCookieManager.matchRequestCookies(realUrl);
            for (MyCookie myCookie : myCookies) {
                cookieBuilder.append(myCookie.name)
                        .append("=")
                        .append(myCookie.value)
                        .append(";");
            }
            String cookie = cookieBuilder.substring(0, cookieBuilder.length() - 1);
            if (!TextUtils.isEmpty(cookie))
                connection.setRequestProperty("Cookie", cookie);
            Log.d(TAG, "request: url = [" + url + "]");
            Log.d(TAG, "request: method = [" + requestMethod + "]");
            Log.d(TAG, "request: params = [" + params + "]");
            Map<String, List<String>> requestProperties = connection.getRequestProperties();
            for (String key : requestProperties.keySet()) {
                StringBuilder sb = new StringBuilder();
                sb.append("request: ").append(key).append(" = ");
                List<String> values = requestProperties.get(key);
                if (values != null && values.size() > 0) {
                    for (String value : values) {
                        sb.append("[").append(value).append("]");
                    }
                }
                Log.d(TAG, sb.toString());
            }
            // 设置POST方式
            if ("POST".equalsIgnoreCase(requestMethod)) {
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
            // 调用connect()只是建立连接，并不会向服务器传送数据，只要调用getResponseCode()，就不必要调用connect方法
            connection.connect();
            // 获得服务器响应的结果和状态码
            responseCode = connection.getResponseCode();
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            for (String key : headerFields.keySet()) {
                List<String> values = headerFields.get(key);
                if (values != null && values.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(TextUtils.isEmpty(key) ? "response: " : "response: " + key + " = ");
                    for (String value : values) {
                        sb.append("[").append(value).append("]");
                    }
                    Log.d(TAG, sb.toString());
                }
            }
            // 存储cookie
            List<String> cookies = headerFields.get("Set-Cookie");
            if (cookies != null && cookies.size() > 0) {
                myCookieManager.parseResponseCookie(realUrl, cookies);
            }
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
                msg = "网络正常";
            } else {
                is = connection.getErrorStream();
                baos = new ByteArrayOutputStream();
                int len = -1;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                baos.flush();
                result = baos.toString();
            }
        } finally { // 关闭资源
            try {
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
                if (connection != null)
                    connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "response: result = [" + result + "]");
        // 设置网络数据模型
        NetworkData data = new NetworkData();
        data.setCode(responseCode);
        data.setMsg(msg);
        data.setData(result); // 业务层数据
        return data;
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