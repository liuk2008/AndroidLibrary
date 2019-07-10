package com.example.myjava.reflect;

/**
 * Created by liuk on 2018/3/6 0006.
 */

public class AdminDaoImpl extends Admin implements AdminDao {

    private String name = "test";
    public int age;


    public void getName() {

    }

    private void setName(String name) {
        System.out.println("姓名：" + name);
    }

    @Override
    public void register() {
        System.out.println("注册");
    }

    @Override
    public void login() {
        System.out.println("登录");
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AdminDaoImpl{");
        sb.append("name='").append(name).append('\'');
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}
