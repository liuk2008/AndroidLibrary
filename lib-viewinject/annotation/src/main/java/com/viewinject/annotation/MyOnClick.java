package com.viewinject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//  编译时注解
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface MyOnClick {
    int value() default -1; // 主项目中使用

    String resId() default "";  // lib中使用

}
