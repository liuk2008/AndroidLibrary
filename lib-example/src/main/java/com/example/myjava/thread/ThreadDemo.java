package com.example.myjava.thread;

import com.example.myjava.thread.callable.MyCallable;
import com.example.myjava.thread.callable.MyFutureTask;
import com.example.myjava.thread.produce.Consumer;
import com.example.myjava.thread.produce.Producer;
import com.example.myjava.thread.produce.Student;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程测试类
 * Created by Administrator on 2018/4/18.
 */

public class ThreadDemo {

    // 测试FutureTask使用
    public static void testFuture() {
        MyCallable callable = new MyCallable();
        MyFutureTask task = new MyFutureTask(callable);
        ExecutorService es = Executors.newFixedThreadPool(3);
        es.execute(task);

        // cancel(true) 方法的原理是向正在运行任务的线程发送中断指令，即调用运行任务的 Thread 的 interrupt() 方法。
//        SystemClock.sleep(2000);
//        task.cancel(true);
    }

    // 测试线程任务资源共享
    public static void testTask() {
        MyThreadPool pool = MyThreadPool.getInstance();
        MyTask task = new MyTask();
        // 需考虑资源共享问题
        for (int i = 1; i < 50; i++) {
            pool.execute(task);
        }
    }

    public static void testProduce() {
        Student s = new Student(false);
        Consumer c = new Consumer(s);
        Producer p = new Producer(s);

        Thread t1 = new Thread(c);
        Thread t2 = new Thread(p);

        t1.start();
        t2.start();

        Thread t3 = new Thread(c);
        Thread t4 = new Thread(p);

        t3.start();
        t4.start();
    }
}
