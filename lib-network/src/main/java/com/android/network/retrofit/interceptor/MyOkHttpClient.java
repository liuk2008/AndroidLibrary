package com.android.network.retrofit.interceptor;


import android.content.Context;

import com.android.network.common.NetworkStatus;
import com.android.network.common.NetworkUtils;
import com.android.network.error.ErrorException;

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
                        NetworkData data = checkNet(context);
                        if (null != data) {
                            throw new ErrorException(data.getCode(), data.getMsg());
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

    /**
     * 检测网络状态
     * 1、是否连接网络
     * 2、已连接网络，是否可正常访问网络
     */
    private static NetworkData checkNet(Context context) {
        boolean isConnected = NetworkUtils.isNetConnected(context);
        if (!isConnected) {
            NetworkData data = new NetworkData(NetworkStatus.NETWORK_DISCONNECTED.getErrorCode(),
                    NetworkStatus.NETWORK_DISCONNECTED.getErrorMessage(), "");
            NetworkUtils.showToast(context, NetworkStatus.NETWORK_DISCONNECTED.getErrorMessage());
            return data;
        }
        boolean isValidated = NetworkUtils.isNetValidated(context);
        if (!isValidated) {
            NetworkData data = new NetworkData(NetworkStatus.NETWORK_UNABLE.getErrorCode(),
                    NetworkStatus.NETWORK_UNABLE.getErrorMessage(), "");
            NetworkUtils.showToast(context, NetworkStatus.NETWORK_UNABLE.getErrorMessage());
            return data;
        }
        return null;
    }

    public static void isProxy(boolean isProxy) {
        mIsProxy = isProxy;
    }

}
