package com.android.network.header;


/**
 * 创建Cookie
 * 设置相关参数
 */
public class MyCookie {

    private static final String TAG = MyCookie.class.getSimpleName();
    public String name, value, domain, path, version;
    private long expires = 253402300799999L, maxAge = -1L, size = 0;
    private boolean secure = false, httpOnly = false;

    private MyCookie(MyCookie.Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.domain = builder.domain;
        this.path = builder.path;
    }

    // Builder模式
    public static class Builder {
        private String name, value, domain, path, version;
        private long expires = 253402300799999L, maxAge = -1L, size = 0;
        private boolean secure = false, httpOnly = false;

        public MyCookie.Builder setName(String name) {
            this.name = name;
            return this;
        }

        public MyCookie.Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public MyCookie.Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public MyCookie.Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public MyCookie builder() {
            return new MyCookie(this);
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MyCookie{");
        sb.append("name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", domain='").append(domain).append('\'');
        sb.append(", path='").append(path).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", expires=0");
        sb.append(", maxAge=0");
        sb.append(", size=0");
        sb.append(", secure=").append(secure);
        sb.append(", httpOnly=").append(httpOnly);
        sb.append('}');
        return sb.toString();
    }
}
