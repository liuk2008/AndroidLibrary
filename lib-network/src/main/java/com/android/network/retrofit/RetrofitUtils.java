package com.android.network.retrofit;

import androidx.annotation.NonNull;

import com.android.network.callback.Callback;

import java.util.List;

import retrofit2.Call;

public class RetrofitUtils {

    // 发送网络请求，请求执行在子线程
    public static <T> CallAdapter request(@NonNull Call<T> call, @NonNull Callback<T> callback) {
        // 每个Call实例可以且只能执行一次请求，不能使用相同的对象再次执行execute() 或enqueue()。
        CallAdapter<T> callAdapter = new CallAdapter<>(call, callback);
        call.enqueue(callAdapter);
        return callAdapter;
    }

    // 取消网络请求
    public static void cancelTask(@NonNull CallAdapter callAdapter) {
        if (callAdapter != null)
            callAdapter.cancel();
    }

    // 批量取消网络请求
    public static void cancelAll(@NonNull List<CallAdapter> taskList) {
        if (taskList.size() <= 0) return;
        // 集合类添加元素后，将会持有元素对象的引用，导致该元素对象不能被垃圾回收，从而发生内存泄漏。
        for (int i = 0; i < taskList.size(); i++) {
            CallAdapter callAdapter = taskList.get(i);
            if (callAdapter != null)
                callAdapter.cancel();
        }
        //  清空，防止内存泄漏
        taskList.clear();
        taskList = null;
    }

}
