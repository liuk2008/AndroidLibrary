package com.android.common.utils.system;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Iterator;
import java.util.List;

public class ContextUtils {

    private static Application application;
    static Activity sFrontActivity;
    private static boolean isAppInFront;
    private static Application.ActivityLifecycleCallbacks callback = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            sFrontActivity = activity;
            isAppInFront = true;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            isAppInFront = false;
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    };

    /**
     * 应用及设备信息
     */
    public static Application getInstance() {
        return application;
    }

    //获取栈顶的Activity
    @Nullable
    public static Activity getFrontActivity() {
        return sFrontActivity;
    }

    //APP是否在前台
    public static boolean isAppInFront() {
        return isAppInFront;
    }

    public static void init(Application app) {
        application = app;
        application.registerActivityLifecycleCallbacks(callback);
    }

    /**
     * 获取当前进程的名字，一般就是当前app的包名
     *
     * @return 返回进程的名字
     */
    public static boolean isInMainProcess() {
        // Returns the identifier of this process
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List list = null;
        if (activityManager != null) {
            list = activityManager.getRunningAppProcesses();
            if (null != list) {
                Iterator i = list.iterator();
                while (i.hasNext()) {
                    ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
                    try {
                        if (info.pid == pid) {
                            // 根据进程的信息获取当前进程的名字
                            return getInstance().getPackageName().equals(info.processName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
