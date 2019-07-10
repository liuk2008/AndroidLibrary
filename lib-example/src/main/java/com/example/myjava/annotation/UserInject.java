package com.example.myjava.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Retention(RetentionPolicy.SOURCE) 1、自定义注解类只会在源码中出现，当源码编译成字节码时注解信息会被清除
 * @Retention(RetentionPolicy.CLASS) 2、自定义注解类被编译在字节码中，当虚拟机加载该字节码时注解信息会被清除
 * @Retention(RetentionPolicy.RUNTIME) 3、自定义注解类会被加载到虚拟机中
 * <p>
 * @Target(ElementType.FIELD) 用于限制当前自定义注解类作用的对象
 * <p>
 * Created by liuk on 2018/3/6 0006.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface UserInject {
    int age();

    String name();
}
