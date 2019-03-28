package com.android.common.utils.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPrefUtils {

    /**
     * 向SharedPreferences文件中存储的信息
     *
     * @param context
     * @param key     向SharedPreferences文件中存储key值
     * @param value   向SharedPreferences文件中存储value值
     */
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 获取SharedPreferences文件中存储的信息
     *
     * @param context
     * @param key     获取SharedPreferences文件中的key值
     * @return
     */
    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 向SharedPreferences文件中存储的信息
     *
     * @param context
     * @param key     向SharedPreferences文件中存储key值
     * @param value   向SharedPreferences文件中存储value值
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 获取SharedPreferences文件中存储的信息
     *
     * @param context
     * @param key     获取SharedPreferences文件中的key值
     * @return
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * 删除SharedPreferences文件中存储的信息
     *
     * @param context
     * @param key     要删除SharedPreferences文件中的key值
     */
    public static void removeString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 向SharedPreferences文件中存储的信息
     *
     * @param context
     * @param key     向SharedPreferences文件中存储key值
     * @param value   向SharedPreferences文件中存储value值
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 获取SharedPreferences文件中存储的信息
     *
     * @param context
     * @param key     获取SharedPreferences文件中的key值
     * @return
     */
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences("config",
                Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

}
