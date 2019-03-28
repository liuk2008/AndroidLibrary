package com.android.common.utils.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class LogUtils {

    private static final String TAG = "LogUtils";
    private static final String ENABLE_SAVELOG_FLAG_FOLDER = "bd56app1234567890savelog";
    private static final boolean sEnableLog = true;
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static boolean sIsSaveLog = false;
    /**
     * 当前进程信息实例
     */
    static RunningAppProcessInfo curRunningProcessInfo;
    private static String APP_TAG = "plusAll";
    private static String SAVELOG_FILE_NAME = "RunningLog";
    private static boolean initFlag = true;
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSS");

    /**
     * 初始化并且得到当前进程信息
     */
    public static void initCurProcess(Activity activity) {
        @SuppressWarnings("static-access")
        ActivityManager am = (ActivityManager) activity.getSystemService(activity.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        for (int i = 0; i < processList.size(); i++) {
            if (processList.get(i).processName.equals("com.aerozhonghuan.bdwuliu")) {
                curRunningProcessInfo = processList.get(i);
                return;
            }
        }
    }

    private static boolean checkInit() {
        if (!initFlag) {
            Log.d(TAG, "Warring：You need init first！(eg:call LogUtils.init())");
        }
        return initFlag;
    }

    public static void init(Context context, String appTag) {
        Log.d(TAG, "init ...");
        if (isEmpty(appTag)) {
            appTag = "My_APP";
        }

        APP_TAG = appTag;
        SAVELOG_FILE_NAME = appTag;

        Log.d(TAG, "init ...APP_TAG：" + APP_TAG);
        Log.d(TAG, "init ...SAVELOG_FILE_NAME：" + SAVELOG_FILE_NAME);

        initFlag = true;

        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                    && hasExternalStoragePermission(context)) {
                String sdCardDir = getExternalStorageDirectory();
                File saveLogFlagFile = new File(sdCardDir + ENABLE_SAVELOG_FLAG_FOLDER);
                if (saveLogFlagFile.exists()) {
                    Log.d(TAG, "Save log flag is true");
                    LogUtils.sIsSaveLog = true;
                }
            }
        } catch (Exception e) {
            printException(e);
        }
    }

    public static String getProcessInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append(getUid() + ",");
        sb.append(getPid() + ",");
        sb.append(getTid() + "  ");
        return sb.toString();
    }

    public static String getPid() {
        return "Pid:" + Process.myPid();
    }

    public static String getTid() {
        return "Tid:" + Process.myTid();
    }

    public static String getUid() {
        return "Uid:" + Process.myUid();
    }

    public static void log(String tag, String msg) {
        if (!checkInit()) {
            return;
        }
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.i(APP_TAG + "." + tag, "" + msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "i"));
                } catch (Exception e) {
                    printException(e);
                }
            }
        }
    }

    public static void logv(String tag, String msg) {
        if (!checkInit()) {
            return;
        }
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.v(APP_TAG + "." + tag, "" + msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "V"));
                } catch (Exception e) {
                    printException(e);
                }
            }
        }
    }

    public static void logd(String tag, String msg) {
        if (!checkInit()) {
            return;
        }
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.d(APP_TAG + "." + tag, msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "D"));
                } catch (Exception e) {
                    printException(e);
                }
            }
        }
    }

    public static void loge(String tag, String msg) {
        if (!checkInit()) {
            return;
        }
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.e(APP_TAG + "." + tag + ".E", "" + msg);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "E"));
                } catch (Exception e) {
                    printException(e);
                }
            }
        }
    }

    public static void loge(String tag, String msg, Throwable e) {
        if (!checkInit()) {
            return;
        }
        if (sEnableLog) {
            if (null == msg) {
                msg = "";
            }
            Log.e(APP_TAG + "." + tag, "" + msg, e);
            if (sIsSaveLog) {
                try {
                    saveToSDCard(formatLog(msg, tag, "E"));
                } catch (Exception e1) {
                    printException(e1);
                }
            }
        }
    }

    public static void saveToSDCard(String message) throws Exception {
        if (!checkInit()) {
            return;
        }
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String sdCardDir = getExternalStorageDirectory();
                File file = new File(sdCardDir + File.separator + ENABLE_SAVELOG_FLAG_FOLDER,
                        SAVELOG_FILE_NAME + getFileNameSuffix() + ".txt");
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                raf.seek(file.length());
                raf.write(message.getBytes());
                raf.close();
            }
        } catch (Exception e) {
            printException(e);
        }
    }

    public static String getFunctionName() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(Thread.currentThread().getStackTrace()[3].getMethodName());
            sb.append("()");
            sb.append(" ");
        } catch (Exception e) {
            printException(e);
        }
        return sb.toString();
    }

    public static String getThreadName() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(getPid() + ",");
            sb.append(Thread.currentThread().getName());
            sb.append("-> ");
            sb.append(Thread.currentThread().getStackTrace()[3].getMethodName());
            sb.append("()");
            sb.append(" ");
        } catch (Exception e) {
            printException(e);
        }
        return sb.toString();
    }

    public static String getThreadStack() {
        StringBuffer sb = new StringBuffer();
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement e : stackTraceElements) {
                sb.append(e.toString());
                sb.append("\n");
            }
        } catch (Exception e) {
            printException(e);
        }
        return sb.toString();
    }

    private static String formatLog(String log, String type, String level) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        synchronized (sFormatter) {
            builder.append(sFormatter.format(Calendar.getInstance().getTime()));
        }
        builder.append("][");
        builder.append(type);
        builder.append("][");
        builder.append(level);
        builder.append("]");
        builder.append(log);
        builder.append("\n");
        return builder.toString();
    }

    private static String getExternalStorageDirectory() {
        String rootpath = Environment.getExternalStorageDirectory().getPath();
        if (!rootpath.endsWith(File.separator)) {
            rootpath += File.separator;
        }
        // Log.d(TAG, "getExternalStorageDirectory() path = " + rootpath);
        return rootpath;
    }

    public static String getClassName() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("-> ");
            sb.append(Thread.currentThread().getStackTrace()[2].getClassName());
            sb.append(".");
        } catch (Exception e) {
            printException(e);
        }
        return sb.toString();
    }

    public static void printException(Exception e) {
        if (null != e) {
            e.printStackTrace();
        }
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    // Empty checks
    // -----------------------------------------------------------------------

    /**
     * <p>
     * Checks if a CharSequence is empty ("") or null.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * <p>
     * <p>
     * NOTE: This method changed in Lang version 2.0. It no longer trims the
     * CharSequence. That functionality is available in isBlank().
     * </p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to
     * isEmpty(CharSequence)
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>
     * Checks if a CharSequence is not empty ("") and not null.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     * @since 3.0 Changed signature from isNotEmpty(String) to
     * isNotEmpty(CharSequence)
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    private static String getFileNameSuffix() {
        StringBuffer sb = new StringBuffer();
        Calendar now = Calendar.getInstance();
        sb.append("_");
        sb.append(now.get(Calendar.YEAR) + "-");
        sb.append(now.get(Calendar.MONTH) + 1 + "-");
        sb.append(now.get(Calendar.DAY_OF_MONTH));
        return sb.toString();
    }
}
