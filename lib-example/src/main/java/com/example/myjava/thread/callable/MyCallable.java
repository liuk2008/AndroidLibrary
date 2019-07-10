package com.example.myjava.thread.callable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * 线程任务，可以返回任务执行结果
 * Created by Administrator on 2018/4/13.
 */

public class MyCallable implements Callable<Integer> {

    private static final String TAG = MyCallable.class.getSimpleName();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    public Integer call() throws Exception {
        System.out.println(Thread.currentThread().getName() + "--->任务开始：" + sdf.format(new Date()));
        int sum = 0;
        long num = 1000000033L;
        for (int i = 0; i < num; i++) {
            if (i % 10000 == 0) {
                sum = sum + i;
            }
        }
        System.out.println(Thread.currentThread().getName() + "--->任务结束：" + sdf.format(new Date()));
        return sum;
    }
}
