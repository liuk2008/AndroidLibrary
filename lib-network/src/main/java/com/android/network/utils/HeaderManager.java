package com.android.network.utils;


import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Header管理类
 * 对Http/Https请求进行Header的写入操作
 */
public class HeaderManager {

    private static final String TAG = HeaderManager.class.getSimpleName();
    private static HeaderManager headerManager;
    private Map<String, String> headers = new HashMap<>();

    private HeaderManager() {
    }

    public static HeaderManager getInstance() {
        if (headerManager == null)
            headerManager = new HeaderManager();
        return headerManager;
    }

    /**
     * 添加Header
     *
     * @param key   header key
     * @param value header value
     */
    public void addHeader(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value))
            headers.put(key, value);
    }

    /**
     * 获取header值
     **/
    public Map<String, String> getHeader() {
        return headers;
    }

    /**
     * 清除cookie
     */
    public void clearCookie() {
        headers.clear();
    }

}
