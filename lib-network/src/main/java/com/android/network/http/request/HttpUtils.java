package com.android.network.http.request;

import android.support.annotation.NonNull;

import com.android.network.callback.Callback;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * http请求工具类
 */
public class HttpUtils {

    private static Executor executor = Executors.newCachedThreadPool();

    public static <T> HttpTask doGet(@NonNull HttpParams httpParams, @NonNull Callback<T> mCallback) {
        HttpTask<T> task = new HttpTask<>(httpParams, mCallback, "GET");
        task.executeOnExecutor(executor);
        return task;
    }

    public static <T> HttpTask doPost(@NonNull HttpParams httpParams, @NonNull Callback<T> mCallback) {
        HttpTask<T> task = new HttpTask<>(httpParams, mCallback, "POST");
        task.executeOnExecutor(executor);
        return task;
    }

    // 取消单个网络请求
    public static void cancelTask(@NonNull HttpTask task) {
        if (task != null && !task.isCancelled())
            task.cancel();
    }

    // 批量取消网络请求
    public static void cancelAll(@NonNull List<HttpTask> taskList) {
        if (taskList.size() <= 0) return;
        // 集合类添加元素后，将会持有元素对象的引用，导致该元素对象不能被垃圾回收，从而发生内存泄漏。
        for (int i = 0; i < taskList.size(); i++) {
            HttpTask task = taskList.get(i);
            if (task != null && !task.isCancelled())
                task.cancel();
        }
        //  清空，防止内存泄漏
        taskList.clear();
        taskList = null;
    }

}
