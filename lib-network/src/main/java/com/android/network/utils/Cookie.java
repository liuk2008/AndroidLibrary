package com.android.network.utils;


/**
 * 设置cookie数据
 */
public class Cookie {

    private static final String TAG = Cookie.class.getSimpleName();
    public String name, value, domain, path;

    private Cookie(Cookie.Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.domain = builder.domain;
        this.path = builder.path;
    }

    // Builder模式
    public static class Builder {
        private String name, value, domain, path;

        public Cookie.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Cookie.Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Cookie.Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Cookie.Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Cookie builder() {
            return new Cookie(this);
        }

    }


}
