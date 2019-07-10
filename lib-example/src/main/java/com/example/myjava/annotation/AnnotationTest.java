package com.example.myjava.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 注解与反射结合使用
 * Created by liuk on 2018/3/6 0006.
 */

public class AnnotationTest {

    public static void annotation() throws Exception {

       User user = new User();

        // 1、获取类的字节码文件对象
        Class clazz = Class.forName("com.freestyle.myjava.annotation.User");

        // 2、获取当前字段上的注解类信息
        Field declaredFieldAge = clazz.getDeclaredField("age");
        UserInject userInject = declaredFieldAge.getAnnotation(UserInject.class);

        // 3、获取自定义注解类上的参数
        int age = userInject.age();
        String name = userInject.name();

        // 4、通过反射机制赋值
        Field declaredFieldName = clazz.getDeclaredField("name");

        // 暴力破解
        declaredFieldAge.setAccessible(true);
        declaredFieldName.setAccessible(true);

        // 向变量赋值
        declaredFieldAge.set(user, age);
        declaredFieldName.set(user, name);

        Method eat = clazz.getDeclaredMethod("eat", String.class);
        eat.setAccessible(true);
        Object result = eat.invoke(user, "米饭");
        // 打印
        System.out.println("user:" + user);
        System.out.println("result:" + result);
    }
}
