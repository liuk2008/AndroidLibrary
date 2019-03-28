package com.android.network.retrofit.interceptor;


import android.content.Context;
import android.text.TextUtils;

import com.android.network.NetStatus;
import com.android.network.NetUtils;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 设置Retrofit请求cookie作用域
 */
public class MyHttpCookieInterceptor implements CookieJar {

    private static final String TAG = MyHttpCookieInterceptor.class.getSimpleName();
    private static MyHttpCookieInterceptor instance;
    private Context mContext;

    private MyHttpCookieInterceptor(Context context) {
        mContext = context;
    }

    public static MyHttpCookieInterceptor getInstance(Context context) {
        // 双重校验锁 在JDK1.5之后，双重检查锁定才能够正常达到单例效果。
        if (null == instance) {
            synchronized (MyHttpHeaderInterceptor.class) {
                if (null == instance) {
                    instance = new MyHttpCookieInterceptor(context);
                }
            }
        }
        return instance;
    }


    // 设置RequestHeader中的cookie
    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookieList = new ArrayList<>();
        //  判断是否存在token
        String token = NetUtils.getString(mContext, NetStatus.Type.TOKEN_KEY, "");
        if (!TextUtils.isEmpty(token)) {
            //  TODO 将token设置在cookie中
            Cookie.Builder builder = new Cookie.Builder();
            String domain = NetUtils.getString(mContext, NetStatus.Type.DOMAIN_KEY, "");
            builder.domain(domain);  // cookie的作用域
            builder.name(NetStatus.Type.TOKEN_KEY);
            builder.value(token);
            Cookie tokenCookie = builder.build();
            cookieList.add(tokenCookie);
        }
//        Cookie VERSION_COOKIE = new Cookie.Builder().domain("lawcert.com").name("version").value("1.0").build();
//        cookieList.add(VERSION_COOKIE);
        return cookieList;
    }

    // 获取ResponseHeader中的cookie
    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            if (NetStatus.Type.TOKEN_KEY.equalsIgnoreCase(cookie.name())) {
                // TODO 本地持久化存储token
                NetUtils.putString(mContext, NetStatus.Type.TOKEN_KEY, cookie.value());
                NetUtils.putString(mContext, NetStatus.Type.DOMAIN_KEY, cookie.domain());
                break;
            }
        }
    }


}
