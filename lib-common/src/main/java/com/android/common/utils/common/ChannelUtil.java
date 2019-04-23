package com.android.common.utils.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.android.common.utils.exception.ExceptionManager;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ChannelUtil {
    private static final String XML_NAME = "channel";
    private static final String DEFAULT_CHANNEL = "official";

    /**
     * 获取渠道标识
     */
    public static String getChannel(Context context) {
        SharedPreferences sp = getChannelSp(context);
        String version = DEFAULT_CHANNEL;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            ExceptionManager.handle(e);
        }
        // 从sp中获取
        String channel = sp.getString(version, null);
        if (TextUtils.isEmpty(channel)) {
            //从apk中获取
            channel = getChannelFromApk(context);
        }
        if (!TextUtils.isEmpty(channel)) {
            //保存sp中备用
            getChannelSp(context).edit().putString(version, channel).apply();
            return channel;
        }
        //全部获取失败
        return channel;
    }


    private static SharedPreferences getChannelSp(Context context) {
        return context.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 从apk中获取版本信息
     */
    private static String getChannelFromApk(Context context) {
        try {
            //从apk包中获取
            ApplicationInfo appinfo = context.getApplicationInfo();
            String sourceDir = appinfo.sourceDir;
            //默认放在meta-inf/里， 所以需要再拼接一下
            String key = "META-INF/Channel";
            String ret = "";
            ZipFile zipfile = null;
            try {
                zipfile = new ZipFile(sourceDir);
                Enumeration<?> entries = zipfile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = ((ZipEntry) entries.nextElement());
                    String entryName = entry.getName();
                    if (entryName.startsWith(key)) {
                        ret = entryName;
                        break;
                    }
                }
            } catch (IOException e) {
                ExceptionManager.handle(e);
            } finally {
                if (zipfile != null) {
                    try {
                        zipfile.close();
                    } catch (IOException e) {
                        ExceptionManager.handle(e);
                    }
                }
            }
            if (TextUtils.isEmpty(ret))
                return DEFAULT_CHANNEL;
            return ret.substring(key.length() + 1).trim();
        } catch (Throwable e) {
            ExceptionManager.handle(e);
            return DEFAULT_CHANNEL;
        }
    }

}
