package com.example.myjava.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by liuk on 2018/3/6 0006.
 */

public class MyInvocationHandler implements InvocationHandler {

    private Object target; // 目标对象

    public MyInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(target, args);
        System.out.println("日志");
        return result; // 代理对象
    }
}
