package com.android.common.utils.exception;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * crash的发生是由于 主线程有未捕获的异常
 */
public class ExceptionManager {

    private static ExecutorService cacheThreadExecutorService = Executors.newCachedThreadPool();
    private static ThrowableHandler mThrowableHandler;

    public static void init(ThrowableHandler throwableHandler) {
        mThrowableHandler = throwableHandler;
        setDefaultUncaughtExceptionHandler();
    }

    /**
     * 用ExceptionManager.handle(e) 来代替捕捉异常后的随意 e.printStackTrace()
     */
    public static void handle(final Throwable throwable) {
        if (null == mThrowableHandler)
            throw new RuntimeException("未初始化 ExceptionManager");
        // 异步处理异常
        cacheThreadExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mThrowableHandler.handleThrowable(throwable);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    //设置全局异常捕捉
    private static void setDefaultUncaughtExceptionHandler() {
        // 获取系统默认的异常处理器
        final Thread.UncaughtExceptionHandler originHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置CrashHandler处理器
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                originHandler.uncaughtException(t, e);
                // 退出程序
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });
    }

}
