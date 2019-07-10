package com.android.network.retrofit.interceptor;


import android.text.TextUtils;

import com.android.network.header.MyCookie;
import com.android.network.header.MyCookieJar;
import com.android.network.header.MyCookieManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 设置Retrofit请求cookie作用域
 * 1、Retrofit解析cookie存在问题
 * Set-Cookie: token=2E61EE077BE24D90AE95C8DC4B6860ED.023DED37E25223F6A5916CF7AC3C2B71;Version=1;Domain=.lawcert.com;Path=/;HttpOnly
 * 解析值：name=token,value=2E61EE077BE24D90AE95C8DC4B6860ED.023DED37E25223F6A5916CF7AC3C2B71,domain=.lawcert.com.path=/
 * Set-Cookie: Version=1;token=2E61EE077BE24D90AE95C8DC4B6860ED.023DED37E25223F6A5916CF7AC3C2B71;Domain=.lawcert.com;Path=/;HttpOnly
 * 解析值：name=Version,value=1,domain=.lawcert.com.path=/
 * 2、Retrofit设置cookie值存在问题，以下格式发送cookie成功
 * Cookie cookie = new Cookie.Builder().domain("xxx.xx.xx").name("version").value("1.0").build();
 * 3、创建cookie时，必须设置domain且domain值不能以点开头，否则cookie创建失败
 */
public class MyHttpCookieInterceptor implements CookieJar {

    private static final String TAG = MyHttpCookieInterceptor.class.getSimpleName();
    private static MyHttpCookieInterceptor instance;
    private MyCookieManager myCookieManager;

    private MyHttpCookieInterceptor() {
        myCookieManager = MyCookieManager.getInstance();
    }

    public static MyHttpCookieInterceptor getInstance() {
        // 双重校验锁 在JDK1.5之后，双重检查锁定才能够正常达到单例效果。
        if (null == instance) {
            synchronized (MyHttpCookieInterceptor.class) {
                if (null == instance) {
                    instance = new MyHttpCookieInterceptor();
                }
            }
        }
        return instance;
    }


    // 设置RequestHeader中的cookie
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookieList = new ArrayList<>();
        if (!myCookieManager.isSetCookie()) return cookieList;
        MyCookieJar myCookieJar = myCookieManager.getMyCookieJar();
        if (null == myCookieJar) return cookieList;
        List<MyCookie> myCookies = myCookieJar.cookieForRequest(url.toString());
        if (myCookies == null || myCookies.size() <= 0) return cookieList;
        for (MyCookie myCookie : myCookies) {
            if (TextUtils.isEmpty(myCookie.name) || TextUtils.isEmpty(myCookie.value))
                continue;
            boolean isMatch = myCookieManager.matchDomain(url.toString(), myCookie);
            if (isMatch) {
                Cookie.Builder builder = new Cookie.Builder();
                builder.name(myCookie.name)
                        .value(myCookie.value);
                if (TextUtils.isEmpty(myCookie.domain))
                    builder.domain(url.host());
                else
                    builder.domain(myCookie.domain);
                cookieList.add(builder.build());
            }
        }
        return cookieList;
    }

    // 获取ResponseHeader中的cookie
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        List<String> myCookies = new ArrayList<>();
        for (Cookie cookie : cookies) {
            StringBuilder builder = new StringBuilder();
            builder.append(cookie.name()).append("=").append(cookie.value()).append(";");
            builder.append("domain").append("=").append(cookie.domain()).append(";");
            builder.append("path").append("=").append(cookie.path());
            myCookies.add(builder.toString());
        }
        myCookieManager.parseResponseCookie(url.toString(), myCookies);
    }


}
