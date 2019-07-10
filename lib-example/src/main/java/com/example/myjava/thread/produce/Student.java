package com.example.myjava.thread.produce;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/4/23.
 */

public class Student {

    private int i = 0, age = 0;
    private String name = "";
    private boolean flag = false;
    private Lock lock = new ReentrantLock();
    private Condition con_pro = lock.newCondition();
    private Condition con_con = lock.newCondition();

    public Student(boolean flag) {
        this.flag = flag;
    }

    public void set() {
        lock.lock();
        try {
            while (flag) {
                con_pro.await();
                // wait();
            }
            if (i % 2 == 0) {
                age = 0;
                name = "test0";
            } else {
                age = 1;
                name = "test1";
            }
            i++;
            flag = true;
            // notify();
            con_con.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void get() {
        lock.lock();
        try {
            while (!flag) {
                // wait();
                con_con.await();
            }
            System.out.println("name:" + name + ",age:" + age);
            flag = false;
            // notify();
            con_pro.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
