package com.android.network.utils;


import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Cookie管理类
 * 对Http/Https请求进行Cookie的写入操作
 * 1、设置cookie时，会先检测cookie的domain是否和url网址的域名一致，如果不一致设置cookie失败
 * 2、只有cookie的domain和path与请求的url匹配才会发送这个cookie
 * 3、cookie中的path其实就是url发布的路径，比如：
 * https://xxx/foo/bar/index，path=/foo/bar
 * https://xxx/foo/index，path=/foo
 * https://xxx/index，path=/
 * https://xxx/foo/bar/index，设置path=/bar，则cookie无法发送
 */
public class CookieManager {

    private static final String TAG = CookieManager.class.getSimpleName();
    private static CookieManager cookieManager;
    private List<Cookie> cookies = new ArrayList<>();

    private CookieManager() {
    }

    public static CookieManager getInstance() {
        if (cookieManager == null)
            cookieManager = new CookieManager();
        return cookieManager;
    }

    /**
     * 添加Cookie
     *
     * @param cookie cookie对象
     */
    public void addCookie(Cookie cookie) {
        if (cookie != null)
            cookies.add(cookie);
    }

    /**
     * 获取cookie值
     *
     * @return cookie
     */
    public String getCookie(String url) {
        if (cookies.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Cookie cookie : cookies) {
                if (TextUtils.isEmpty(cookie.name) || TextUtils.isEmpty(cookie.value))
                    continue;
                boolean isMatch = matchDomain(url, cookie.domain);
                if (isMatch) {
                    builder.append(cookie.name)
                            .append("=")
                            .append(cookie.value)
                            .append(";");
                }
            }
            return builder.substring(0, builder.length() - 1);
        } else {
            return "";
        }
    }

    /**
     * 清除cookie
     */
    public void clearCookie() {
        cookies.clear();
    }


    /**
     * 校验domain
     *
     * @param url    请求的url地址
     * @param domain 设置的域名地址
     * @return 是否匹配
     */
    private boolean matchDomain(String url, String domain) {
        if (TextUtils.isEmpty(domain)) return true;
        String host = "";
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
            host = Uri.parse(url).getHost();
        if (domain.startsWith("."))
            domain = domain.substring(1);
        if (host.equalsIgnoreCase(domain) || host.endsWith(domain))
            return true;
        else
            return false;
    }

    /**
     * 校验path
     * // https://passport.lawcert.com/proxy/account/user/login
     *
     * @param url
     * @param path
     * @return
     */
    private boolean matchPath(String url, String path) {
        if (TextUtils.isEmpty(path)) return true;
        if (!path.startsWith("/"))
            throw new IllegalArgumentException("path must start with '/'");
        if ("/".equalsIgnoreCase(path))
            return true;
        return false;
    }


}
