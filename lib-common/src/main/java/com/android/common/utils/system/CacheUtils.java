package com.android.common.utils.system;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.Process;
import android.os.storage.StorageManager;
import android.support.annotation.RequiresApi;

import java.lang.reflect.Method;


public class CacheUtils {

    private static onCacheListener mOnCacheListener;

    public interface onCacheListener {
        void onCache(long... size);
    }

    public static void getCache(Context context, String packageName, onCacheListener onCacheListener) {
        try {
            mOnCacheListener = onCacheListener;
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion >= Build.VERSION_CODES.O) {
                getCache(context, packageName);
            } else {
                PackageManager pm = context.getPackageManager();
                Method method = pm.getClass().getMethod(
                        "getPackageSizeInfo", String.class,
                        IPackageStatsObserver.class);
                method.invoke(pm, packageName, new MyStatsObserver());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 8.0 以下获取APP 缓存大小、数据大小、应用大小
    private static class MyStatsObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
            try {
                mOnCacheListener.onCache(pStats.cacheSize, pStats.dataSize, pStats.codeSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 8.0 以上获取缓存功能，需要系统权限 android.permission.PACKAGE_USAGE_STATS
    @RequiresApi(Build.VERSION_CODES.O)
    private static void getCache(Context context, String packName) {
        StorageStatsManager statsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);
        try {
            StorageStats stats = statsManager.queryStatsForPackage(StorageManager.UUID_DEFAULT, packName, Process.myUserHandle());
            mOnCacheListener.onCache(stats.getCacheBytes(), stats.getDataBytes(), stats.getAppBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
