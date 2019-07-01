package com.android.common.webview.client;


import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 对系统CookieManager进行Cookie的写入操作
 * 1、Android系统WebView是将cookie存储data/data/package_name/app_webview这个目录下的一个叫Cookies的数据中
 * 2、Cookie 同步方法要在 WebView 的 setting 设置完，loadUrl(url)调用前，进行Cookie操作，否则无效。
 * 3、添加header
 */
public class WebViewUtils {

    private static Map<String, String> headerMap = new HashMap<>();
    private static Map<String, String> cookieMap = new HashMap<>();

    /**
     * 设置 cookie
     */
    public static void setCookie(@Nullable String key, @Nullable String value) {
        if (TextUtils.isEmpty(key)) return;
        if (null == value) value = "";
        cookieMap.put(key, value);
    }

    /**
     * 设置 header
     */
    public static void setHeader(@Nullable String key, @Nullable String value) {
        if (TextUtils.isEmpty(key)) return;
        if (null == value) value = "";
        headerMap.put(key, value);
    }

    /**
     * 获取cookie集合
     */
    public static Map<String, String> getCookieMap() {
        return cookieMap;
    }

    /**
     * 获取header集合
     */
    public static Map<String, String> getHeaderMap() {
        return headerMap;
    }

    /**
     * 清除cookie
     */
    public static void clearCookie() {
        CookieUtil.clearCookie();
        cookieMap.clear();
    }

    /**
     * 清除 header
     */
    public static void clearHeader() {
        headerMap.clear();
    }

}
