package com.example.myjava.thread.produce;

/**
 * Created by Administrator on 2018/4/23.
 */

public class Student1 {

	private int i = 0, age = 0;
	private String name = "";
	private boolean flag = false;

	public Student1(boolean flag) {
		this.flag = flag;
	}

	public synchronized void set() {
		try {
			while (flag) {
				wait();
			}
			if (i % 2 == 0) {
				age = 2;
				name = "test2";
			} else {
				age = 4;
				name = "test4";
			}
			i++;
			flag = true;
			notifyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized void get() {
		try {
			while (!flag) {
				wait();
			}
			Thread.sleep(500);
			System.out.println(Thread.currentThread().getName() + "name:"
					+ name + ",age:" + age);
			flag = false;
			notifyAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
