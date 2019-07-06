package com.android.network.http.request;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.network.NetStatus;
import com.android.network.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


// http请求参数
public class HttpParams {
    private final static String TAG = "http";
    public String url, contentType;
    public Map<String, Object> params;

    private HttpParams(HttpParams.Builder builder) {
        this.url = builder.url;
        this.params = builder.params;
        this.contentType = builder.contentType;
    }

    // Builder模式
    public static class Builder {
        private String url, contentType = "application/x-www-form-urlencoded";
        private Map<String, Object> params = new HashMap<>();

        public HttpParams.Builder setUrl(@NonNull String url) {
            this.url = url;
            return this;
        }

        public HttpParams.Builder setContentType(String contentType) {
            if (!TextUtils.isEmpty(contentType))
                this.contentType = contentType;
            return this;
        }

        public HttpParams.Builder appendParams(Object key, Object value) {
            if (null == value || "".equals(String.valueOf(value))) {
                return this;
            } else {
                params.put(String.valueOf(key), String.valueOf(value));
                return this;
            }
        }

        public HttpParams builder() {
            return new HttpParams(this);
        }

    }

    // 解析字符串，获取cookie值
    public void getCookie(Context mContext, List<String> cookies) {
        for (String cookie : cookies) {
            if (TextUtils.isEmpty(cookie)) return;
            String[] splits = cookie.split(";");
            for (String split : splits) {
                split = split.trim();
                String[] tokens = split.split("=");
                if (NetStatus.Type.TOKEN_KEY.equalsIgnoreCase(tokens[0].trim())) {
                    NetUtils.putString(mContext, NetStatus.Type.TOKEN_KEY, tokens[1].trim());
                    break;
                }
            }
        }
    }

}