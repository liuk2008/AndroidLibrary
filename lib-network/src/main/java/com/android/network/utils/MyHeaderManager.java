package com.android.network.utils;


import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Header管理类
 * 对Http/Https请求进行Header的写入操作
 */
public class MyHeaderManager {

    private static final String TAG = MyHeaderManager.class.getSimpleName();
    private static MyHeaderManager headerManager;
    private Map<String, String> headers = new HashMap<>();

    private MyHeaderManager() {
    }

    public static MyHeaderManager getInstance() {
        if (headerManager == null)
            headerManager = new MyHeaderManager();
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
     * 清除header
     */
    public void clearHeader() {
        headers.clear();
    }

}
