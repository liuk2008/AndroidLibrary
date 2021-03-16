package com.android.network.http.request;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;
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
                // 兼容所有格式
                params.put(String.valueOf(key), value);
                return this;
            }
        }

        public HttpParams builder() {
            return new HttpParams(this);
        }

    }

}