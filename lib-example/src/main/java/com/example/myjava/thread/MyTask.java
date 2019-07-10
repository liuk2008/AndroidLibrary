package com.example.myjava.thread;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 线程池需考虑资源共享问题
 * 1、使用synchronized后，相当于串行执行，降低了效率
 * 2、使用AtomicInteger可以保持数据唯一，且并发执行
 * 3、Semaphore 信号量仅仅是对池资源进行监控，但不保证线程的安全
 * 4、Lock锁、读写锁
 * <p>
 * ThreadLocal的作用是提供线程内的局部变量，这种变量在线程的生命周期内起作用
 * Created by Administrator on 2018/3/20.
 */

public class MyTask implements Runnable {
    private static final String TAG = MyTask.class.getSimpleName();
    private AtomicInteger atomicInteger = new AtomicInteger(100); // 原子类
    private Semaphore semaphore = new Semaphore(1); // 信号量 相当于串行执行
    private Lock lock = new ReentrantLock();
    private int i = 100;

    @Override
    public void run() {
        method3();
    }

    private synchronized void method1() {
        System.out.println(i + "：开始运行" + "，thread name is：" + Thread.currentThread().getName());
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i--;
        System.out.println( i + "：结束运行" + "，thread name is：" + Thread.currentThread().getName());
    }

    private void method2() {
        System.out.println(atomicInteger.get() + "：开始运行" + "，thread name is：" + Thread.currentThread().getName());
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(atomicInteger.decrementAndGet() + "：结束运行" + "，thread name is：" + Thread.currentThread().getName());
    }

    private void method3() {
        try {
            semaphore.acquire(); // 获取一把锁
            System.out.println(i + "：开始运行" + "，thread name is：" + Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i--;
            System.out.println(i + "：结束运行" + "，thread name is：" + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(); // 释放一把锁
        }
    }

    private void method4() {
        try {
            lock.lock();
            System.out.println( i + "：开始运行" + "，thread name is：" + Thread.currentThread().getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i--;
            System.out.println( i + "：结束运行" + "，thread name is：" + Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
    }

}
