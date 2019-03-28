package com.android.common.utils.system;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.List;

public class ActivityUtils {
    /**
     * 获取任务信息
     * 1表示给集合设置的最大容量
     */
    private static ActivityManager.RunningTaskInfo getTaskInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null)
            return null;
        List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(1);
        return taskInfoList.get(0);
    }

    /**
     * 判断某一个Activity类是否存在任务栈里面
     */
    public static boolean isExistActivity(Context context, Class clazz) {
        ActivityManager.RunningTaskInfo taskInfo = getTaskInfo(context);
        if (taskInfo == null)
            return false;
        boolean flag = false;
        Intent intent = new Intent(context, clazz);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        if (cmpName != null) { // 说明系统中存在这个activity
            if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 判断APP是否运行
     */
    public static boolean isRunningApp(Context context, String packageName) {
        // 1、活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null)
            return false;
        boolean isAppRunning = false;
        // 2、 获取当前运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        // 3、 获取包管理器
        PackageManager pm = context.getPackageManager();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            // 4、获取进程名，其实就是包名
            if (packageName.equals(runningAppProcessInfo.processName)) {
                isAppRunning = true;
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 获取栈顶的Activity
     */
    public static boolean isTopActivity(Context context, Class clazz) {
        ActivityManager.RunningTaskInfo taskInfo = getTaskInfo(context);
        ComponentName topActivity = taskInfo.topActivity;
        if (clazz.getName().equals(topActivity.getClassName())) {
            return true;
        }
        return false;
    }

}
