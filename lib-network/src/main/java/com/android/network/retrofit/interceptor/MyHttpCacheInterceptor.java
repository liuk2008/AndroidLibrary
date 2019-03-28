package com.android.network.retrofit.interceptor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 设置Retrofit缓存机制
 * TODO 待验证
 * 1、有网络时，直接请求接口数据
 * 2、无网络时，读取缓存数据
 * Created by Administrator on 2018/2/6.
 */

public class MyHttpCacheInterceptor implements Interceptor {
    private static final String TAG = MyHttpCacheInterceptor.class.getSimpleName();
    private Context context;

    public MyHttpCacheInterceptor(Context context) {
        this.context = context;
    }

    public Response intercept(Chain chain) throws IOException {
        /*
         * 对request的设置用来指定有网/无网下所走的方式
         * 1、无网络下强制使用缓存，无论缓存是否过期，此时该请求实际上不会被发送出去。
         * 2、有网络时发出请求
         */
        Request request = chain.request();
        if (!isNetConnected(context))
            request = request.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + 30) // 当缓存中有副本文件存在，客户端才会获取副本，缓存30s
//                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();

        return chain.proceed(request);

//        Response response = chain.proceed(request);
//        response = response.newBuilder()
//                .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
//                .header("Cache-Control", "public, max-age=0") // 服务器通知客户端缓存数据
//                .build();
//        return response;
    }

    private boolean isNetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();//  获取可用网络
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

}
