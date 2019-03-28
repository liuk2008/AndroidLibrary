package com.viewject.processor;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.lang.model.element.Modifier;

/**
 * 动态生成Java文件
 * 1、TypeSpec用于创建类或者接口，FieldSpec用来创建字段，MethodSpec用来创建方法和构造函数，ParameterSpec用来创建参数，AnnotationSpec用于创建注解
 * 2、$T for Types $N for 变量 $L for 数值
 * return (Date.class)
 * addStatement("return new $T()", Date.class) // 方法内容
 */
public class TestJavapoet {
    public void test() {
        // 构建方法
        MethodSpec methodmain = MethodSpec.methodBuilder("main")  // 方法名
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC) // 修饰符
                .returns(void.class) // 返回值
                .addParameter(String[].class, "args") //形参类型
                .addStatement("$T.out.print($S)", System.class, "hello javapoet") // 方法内容
                .build();
        //  构建类
        TypeSpec typeMain = TypeSpec.classBuilder("MainPP")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(methodmain) // 添加方法
                .build();

        //  构建Java文件，注意包名
        JavaFile javaFile = JavaFile.builder("com.mingwei.japdemo", typeMain).build();

        // 输出文件
        try {
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
