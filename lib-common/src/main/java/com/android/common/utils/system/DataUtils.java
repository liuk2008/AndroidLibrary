package com.android.common.utils.system;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Context.getExternalFilesDir() -- SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
 * Context.getExternalCacheDir() -- SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
 * Created by Administrator on 2017/9/26.
 */
public class DataUtils {

    public static String getCacheSize(Context context) {
        long cacheSize = getFileSize(context.getCacheDir());
        cacheSize += getFileSize(context.getFilesDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += cacheSize + getFileSize(context.getExternalCacheDir());
            cacheSize += getFileSize(context.getExternalFilesDir(null));
        }
        return formatSize(cacheSize);
    }

    /**
     * 清除app缓存（包括webview）
     */
    public static boolean clearCache(Context context) {
        try {
            boolean clear = deleteFile(context.getExternalCacheDir())
                    && deleteFile(context.getExternalFilesDir(null))
                    && deleteFile(context.getCacheDir())
                    && deleteFile(context.getFilesDir());
//            new WebView(context).clearCache(true);
            return clear;
        } catch (Throwable e) {
            return false;
        }
    }

    private static long getFileSize(File file) {
        try {
            if (file.isFile()) {
                return file.length();
            } else {
                long size = 0;
                File[] files = file.listFiles();
                for (File f : files) {
                    size += getFileSize(f);
                }
                return size;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 格式化单位
     */
    private static String formatSize(double size) {
        int count = -1;
        double result = size;
        for (long i = (long) size; i > 0; i /= 1000) {
            count += 1;
        }
        String unit;
        if (count <= 1) {
            count = 1;
            unit = "KB";
        } else if (count == 2) {
            unit = "MB";
        } else if (count == 3) {
            unit = "GB";
        } else if (count == 4) {
            unit = "TB";
        } else {
            count = 5;
            unit = "PB";
        }
        for (int i = 0; i < count; i++) {
            result = result / 1000;
        }
        return String.format("%.2f", result) + unit;
    }

    private double formatNumber(double size) {
        double result = size / 1000;
        if (size > 1000) {
            result = size / 1000;
            if (result > 1000) {
                result = formatNumber(result);
            }
        }
        return result;
    }

    /**
     * * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * *
     *
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        deleteFile(context.getCacheDir());
        deleteFile(context.getExternalCacheDir());
    }

    /**
     * * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * *
     *
     * @param context
     */
    public static void cleanDatabases(Context context) {
        deleteFile(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) *
     *
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        deleteFile(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }


    public static boolean deleteFile(File file) {
        try {
            if (!file.exists()) {
                return true;
            } else if (file.isFile()) {
                return file.delete();
            } else {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (!deleteFile(f)) {
                        return false;
                    }
                }
                return file.delete();
            }
        } catch (Exception e) {
            return false;
        }
    }

}
