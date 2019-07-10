package com.example.myjava.annotation;


/**
 * 注解
 * 1、声明 语法：类型 变量名称 ();	如果属性没有指定默认值的话，那么你再使用注解的时候，必须要给属性赋值。
 * 2、可以定义六种类型的属性：int，String，Class，注解类型，枚举类型，一维数组类型
 * 3、声明属性的时候，可以指定属性的默认值; 类型 属性名称 () default 值;如果指定了默认值，属性就不用赋值了。
 * 4、声明属性名称是value，那么value=  可以省略不写了，但是如果同时存在value与其他属性，value= 必须存在
 *
 * @author Administrator
 */
@Anno1(a = 1, s = "haha", c = AnnoDemo.class, a2 = @Anno2(b = 10), c2 = Color.RED, arrs = {"abc", "345"})
@Anno3
@Anno4(value = "hehe", d = 10)
public class AnnoDemo {

}

/**
 * 自定义注解
 * 定义属性：类型 变量名称 ();
 * 总共六种类型
 */
@interface Anno1 {
    int a();

    String s();

    Class c();

    // 注解类型的
    Anno2 a2();

    // 定义枚举类型
    Color c2();

    // 以上数据类型的一维数组
    String[] arrs();
}

// 注解也可以包含属性
@interface Anno2 {
    int b();
}

// 枚举类型
enum Color {
    RED, GREEN
}


// 新的注解
@interface Anno3 {
    int c() default 5;
}

// 如果是属性名称是value，那么value=  可以省略不写了
@interface Anno4 {
    // 属性名称是value,没有指定默认值
    String value();

    int d();
}














