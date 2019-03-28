package com.android.common.utils.exception;

import android.os.Environment;
import android.util.Log;


import com.android.common.utils.common.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ThrowableConfigure {
    private static final String TAG = ThrowableConfigure.class.getSimpleName();

    public static void init() {
        initExceptionManager();
    }

    // 初始化异常配置
    private static void initExceptionManager() {
        ExceptionManager.init(new ThrowableHandler() {
            @Override
            public void handleThrowable(Throwable t) {
                StringBuilder sb = new StringBuilder();
                sb.append(t.getClass().getName()).append("---").append(t.getMessage()).append('\n');
                Throwable throwable = t;
                sb.append("ErrorLog：").append(Log.getStackTraceString(t));
                while (throwable.getCause() != null) {
                    throwable = throwable.getCause();
                    if (throwable instanceof UnknownHostException) {
                        sb.append(throwable.getMessage());
                    } else {
                        sb.append('\n').append(Log.getStackTraceString(throwable));
                    }
                }
                LogUtils.loge(TAG, sb.toString());
                saveCrashInfo2File(t);
            }
        });
    }

    /**
     * 保存错误信息到文件中
     */
    private static void saveCrashInfo2File(Throwable ex) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = formatter.format(new Date());
        String fileName = "crash-" + date + ".txt";
        // 将异常存入文件中
        PrintWriter err = null;
        FileOutputStream fos = null;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory() + "/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                fos = new FileOutputStream(path + fileName, true);
                err = new PrintWriter(fos, true);
                SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String time = sdfDate.format(new Date(System.currentTimeMillis()));
                err.write("\n" + time + "\n");
                ex.printStackTrace(err);
            }
        } catch (Exception e) {
            LogUtils.loge(TAG, "an error occured while writing file...", e);
        } finally {
            try {
                if (err != null)
                    err.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                LogUtils.loge(TAG, "an error occured while fos is closed...", e);
            }
        }
    }
}
