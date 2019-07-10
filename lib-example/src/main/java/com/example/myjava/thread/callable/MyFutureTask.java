package com.example.myjava.thread.callable;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 在 FutureTask 类中，线程执行完毕以及取消任务后，done()方法会被调用。
 * 1、可以在done()方法中调用get()获取到任务执行结果，且不会阻塞当前线程，done()方法执行在工作线程中
 * 2、调用 FutureTask 类 cancel() 方法后，会抛出 CancellationException 异常，可以在done()方法中捕获进行处理。此时done()方法执行在当前线程中
 * 3、cancel()并不是真正的取消任务，而是调用Thread的Interrupt()方法
 * Created by Administrator on 2018/4/13.
 */

public class MyFutureTask extends FutureTask<Integer> {

    private static final String TAG = MyFutureTask.class.getSimpleName();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    public MyFutureTask(Callable<Integer> callable) {
        super(callable);
    }

    @Override
    protected void done() {
        super.done();
        System.out.println(Thread.currentThread().getName() + "--->done()：");
        try {
            int result = get();
            System.out.println(Thread.currentThread().getName() + "--->" + sdf.format(new Date()) + "--->获取任务结果：" + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (CancellationException e) {
            System.out.println(Thread.currentThread().getName() + "--->任务被取消于：" + sdf.format(new Date()));
        }
    }
}
