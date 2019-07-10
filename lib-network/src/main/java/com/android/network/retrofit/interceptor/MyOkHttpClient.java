package com.android.network.retrofit.interceptor;


import android.content.Context;

import com.android.network.error.ErrorData;
import com.android.network.error.ErrorException;
import com.android.network.network.NetworkStatus;
import com.android.network.network.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * 初始化OkHttpClient
 */
public class MyOkHttpClient {

    private static OkHttpClient instance;
    private static boolean mIsProxy = true;

    public static OkHttpClient getInstance(Context context) {
        if (null == instance) {
            synchronized (MyOkHttpClient.class) {
                if (null == instance) {
                    try {
                        init(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    private static void init(final Context context) {
        // 设置日志
        MyHttpLoggingInterceptor loggingInterceptor = new MyHttpLoggingInterceptor();
        loggingInterceptor.setLevel(MyHttpLoggingInterceptor.Level.BODY);
        // 设置请求头
        MyHttpHeaderInterceptor headerInterceptor = MyHttpHeaderInterceptor.getInstance(context);
        // 设置cookie
        MyHttpCookieInterceptor cookieInterceptor = MyHttpCookieInterceptor.getInstance();
        // 设置缓存
        File cacheDir = new File(context.getCacheDir().getAbsolutePath(), "HttpCache");
        Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);//缓存10m
        MyHttpCacheInterceptor cacheInterceptor = new MyHttpCacheInterceptor(context);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(NetworkStatus.Type.TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .writeTimeout(NetworkStatus.Type.TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .readTimeout(NetworkStatus.Type.TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false) // 禁止重新连接
                .cookieJar(cookieInterceptor) // 设置cookie
//                .connectionPool(new ConnectionPool(0, 1, TimeUnit.SECONDS))
//                .cache(cache) // 设置缓存目录
//                .addInterceptor(cacheInterceptor) // 设置缓存
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        ErrorData errorData = NetworkUtils.checkNet(context);
                        if (null != errorData) {
                            throw new ErrorException(errorData.getCode(), errorData.getMsg());
                        } else {
                            return chain.proceed(chain.request());
                        }
                    }
                })
                .addInterceptor(headerInterceptor)
                .addInterceptor(loggingInterceptor);

        // mIsProxy=true：可以使用代理，mIsProxy=false：禁止使用代理
        if (!mIsProxy)
            builder.proxy(Proxy.NO_PROXY);
        instance = builder.build();
    }

    public static void isProxy(boolean isProxy) {
        mIsProxy = isProxy;
    }

}
