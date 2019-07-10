package com.example.myjava.thread.produce;

/**
 * Created by Administrator on 2018/4/23.
 */

public class Producer implements Runnable {

    private Student student;

    public Producer(Student student) {
        this.student = student;
    }
    @Override
    public void run() {
        while (true) {
            student.set();
        }
    }
}
