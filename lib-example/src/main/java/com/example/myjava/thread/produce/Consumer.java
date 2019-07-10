package com.example.myjava.thread.produce;

/**
 * Created by Administrator on 2018/4/23.
 */

public class Consumer implements Runnable {
    private Student student;

    public Consumer(Student student) {
        this.student = student;
    }

    public Consumer() {

    }

    @Override
    public void run() {
        while (true) {
            student.get();
        }
    }
}
