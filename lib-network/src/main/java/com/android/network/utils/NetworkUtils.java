package com.android.network.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.android.network.NetworkData;

/**
 * 检测网络状态
 * Created by Administrator on 2016/12/5 0005.
 */
public class NetworkUtils {

    /**
     * 检测网络状态
     * 1、是否连接网络
     * 2、已连接网络，是否可正常访问网络
     */
    public static NetworkData checkNet(Context context) {
        boolean isConnected = isNetConnected(context);
        if (!isConnected) {
            NetworkData errorData = new NetworkData();
            errorData.setCode(NetworkStatus.NETWORK_DISCONNECTED.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_DISCONNECTED.getErrorMessage());
            showToast(context, NetworkStatus.NETWORK_DISCONNECTED.getErrorMessage());
            return errorData;
        }
        boolean isValidated = isNetValidated(context);
        if (!isValidated) {
            NetworkData errorData = new NetworkData();
            errorData.setCode(NetworkStatus.NETWORK_UNABLE.getErrorCode());
            errorData.setMsg(NetworkStatus.NETWORK_UNABLE.getErrorMessage());
            showToast(context, NetworkStatus.NETWORK_UNABLE.getErrorMessage());
            return errorData;
        }
        return null;
    }

    /*
     * 判断网络是否连接
     */
    public static boolean isNetConnected(Context context) {
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

    /**
     * 判断当前网络能否访问网络(6.0以上版本)
     * 设置代理：关闭代理工具返回false，打开代理工具返回true
     */
    public static boolean isNetValidated(Context context) {
        boolean isNetUsable = false;
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities =
                    manager.getNetworkCapabilities(manager.getActiveNetwork());
            isNetUsable = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        return isNetUsable;
    }

    private static Toast toast;

    public static void showToast(final Context context, final String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                }
                toast.setText(msg);
                toast.show();
            }
        });
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }


}
