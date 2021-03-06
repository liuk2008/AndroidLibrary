package com.android.network.header;


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
public class MyCookieManager {

    private static final String TAG = "MyCookieManager";
    private static MyCookieManager myCookieManager;
    private MyCookieJar myCookieJar;
    private boolean isSetCookie = true;

    private MyCookieManager() {
    }

    public static MyCookieManager getInstance() {
        if (myCookieManager == null)
            myCookieManager = new MyCookieManager();
        return myCookieManager;
    }

    public void setMyCookieJar(MyCookieJar myCookieJar) {
        this.myCookieJar = myCookieJar;
    }

    /**
     * 是否设置http请求中的cookie
     */
    public void isSetCookie(boolean isSetCookie) {
        this.isSetCookie = isSetCookie;
    }

    /**
     * 校验设置的Request请求Cookie值
     *
     * @param url 请求url
     * @return Cookie集合
     */
    public List<MyCookie> matchRequestCookies(String url) {
        if (!isSetCookie) return null;
        if (null == myCookieJar) return null;
        List<MyCookie> myCookies = myCookieJar.cookieForRequest(url);
        if (myCookies == null || myCookies.size() <= 0) return null;
        for (int i = 0; i < myCookies.size(); i++) {
            MyCookie myCookie = myCookies.get(i);
            if (TextUtils.isEmpty(myCookie.name) || TextUtils.isEmpty(myCookie.value)) {
                myCookies.remove(i);
                continue;
            }
            boolean isMatch = matchDomain(url, myCookie);
            if (!isMatch)
                myCookies.remove(i);
        }
        return myCookies;
    }


    /**
     * 解析Set-Cookie，可能存在多个Set-Cookie响应头
     *
     * @param url     请求url
     * @param cookies cookie集合
     *                token=2E61EE077BE24D90AE95C8DC4B6860ED.023DED37E25223F6A5916CF7AC3C2B71;Version=1;Domain=.lawcert.com;Path=/;HttpOnly
     */
    public void parseResponseCookie(String url, List<String> cookies) {
        if (myCookieJar == null) return;
        List<MyCookie> myCookies = new ArrayList<>();
        for (String cookie : cookies) {
            if (!TextUtils.isEmpty(cookie)) { // 解析Set-Cookie，生成自定义MyCookie
                MyCookie.Builder builder = new MyCookie.Builder();
                if (cookie.contains(";")) {
                    String[] values = cookie.split(";");
                    for (String value : values) {
                        createCookie(value, builder);
                    }
                } else
                    createCookie(cookie, builder);
                MyCookie myCookie = builder.builder();
                myCookies.add(myCookie);
            }
        }
        myCookieJar.cookieFromResponse(url, myCookies);

    }


    private void createCookie(String cookie, MyCookie.Builder builder) {
        if (cookie.contains("=")) {
            cookie = cookie.trim();
            String[] strings = cookie.split("=");
            String name = strings[0];
            String value = strings[1];
            if ("domain".equalsIgnoreCase(name)) {
                builder.setDomain(value);
            } else if ("path".equalsIgnoreCase(name)) {
                builder.setPath(value);
            } else if ("version".equalsIgnoreCase(name)) {
            } else if ("expires".equalsIgnoreCase(name)) {
            } else if ("max-age".equalsIgnoreCase(name)) {
            } else if ("secure".equalsIgnoreCase(name)) {
            } else if ("httponly".equalsIgnoreCase(name)) {
            } else {
                builder.setName(name);
                builder.setValue(value);
            }
        }
    }

    /**
     * 校验domain
     *
     * @param url      请求的url地址
     * @param myCookie 设置的cookie
     * @return 是否匹配
     */
    private boolean matchDomain(String url, MyCookie myCookie) {
        String domain = myCookie.domain;
        if (TextUtils.isEmpty(domain))
            return true;
        String host = "";
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme))
            host = Uri.parse(url).getHost();
        if (domain.startsWith("."))
            domain = domain.substring(1);
        myCookie.domain = domain;
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
        return "/".equalsIgnoreCase(path);
    }

}
