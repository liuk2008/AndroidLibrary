package com.android.common.net.retrofit;

import android.support.annotation.NonNull;


import com.android.network.callback.Callback;
import com.android.network.retrofit.CallAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class RetrofitUtils {

    private static Map<Call, CallAdapter> hashMap = new HashMap<>();

    // 发送网络请求，请求执行在子线程
    public static <T> void request(@NonNull Call<T> call, @NonNull Callback<T> callback) {
        // 每个Call实例可以且只能执行一次请求，不能使用相同的对象再次执行execute() 或enqueue()。
        CallAdapter<T> callAdapter = new CallAdapter<>(callback);
        hashMap.put(call, callAdapter);
        call.enqueue(callAdapter);
    }

    // 取消网络请求
    public static void cancelTask(@NonNull Call call) {
        if (call != null && !call.isCanceled()) {
            CallAdapter callAdapter = hashMap.get(call);
            if (callAdapter != null)
                callAdapter.setCallback();
            hashMap.remove(call);
            call.cancel();
            call = null;
        }
    }

    public static void cancelAll(@NonNull List<Call> taskList) {
        if (taskList == null)
            return;
        // 集合类添加元素后，将会持有元素对象的引用，导致该元素对象不能被垃圾回收，从而发生内存泄漏。
        for (int i = 0; i < taskList.size(); i++) {
            Call call = taskList.get(i);
            if (call != null && !call.isCanceled()) {
                CallAdapter callAdapter = hashMap.get(call);
                callAdapter.setCallback();
                hashMap.remove(call);
                call.cancel();
                call = null;
            }
        }
        //  清空，防止内存泄漏
        taskList.clear();
        taskList = null;
    }

}
