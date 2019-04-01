package com.viewinject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//  编译时注解
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface MyBindView {
    int value() default -1;

    String resId() default "";
}
