package com.viewinject.processor;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


/**
 * AnnotatedClass对象，根据注解元素所对应的类名生成，统一管理一个类下所有的注解元素值
 * 作用：
 * 1、控制动态生成Java文件
 * 2、将注解元素添加到AnnotatedClass对象中统一管理
 * <p>
 * 常用Element子类
 * 1、TypeElement：类
 * 2、ExecutableElement：成员方法
 * 3、VariableElement：成员变量
 * <p>
 * 通过包名和类名获取TypeName，TypeName targetClassName = ClassName.get(“PackageName”, “ClassName”);
 * <p>
 * 通过Element获取TypeName，TypeName type = TypeName.get(element.asType());
 * <p>
 * $L for Literals、$S for Strings、$T for Types、$N for Names
 */
public class AnnotatedClass {
    // 注解元素
    private TypeElement typeElement;
    // 元素相关的辅助类
    private Elements mElementUtils;
    //  日志相关的辅助类
    private Messager mMessager;
    // 成员变量集合
    private List<MyBindViewField> mFeild;
    // 成员方法集合
    private List<MyOnclickMethod> mMethod;

    public AnnotatedClass(TypeElement typeElement, Elements mElementUtils, Messager messager) {
        this.typeElement = typeElement;
        this.mElementUtils = mElementUtils;
        this.mMessager = messager;
        mFeild = new ArrayList<>();
        mMethod = new ArrayList<>();
    }

    /**
     * 添加一个成员
     */
    public void addField(MyBindViewField field) {
        mFeild.add(field);
    }

    public void addMethod(MyOnclickMethod method) {
        mMethod.add(method);
    }

    /**
     * 输出Java
     * <p>
     */
    public JavaFile generateFinder() {
        String packageName = getPackageName();
        String className = getClassName();
        TypeName classTypeName = TypeName.get(typeElement.asType()); // 类类型

        // 1、构建类信息，统一管理注解信息  xxxxViewInjector implements ViewInjector<T>
        TypeSpec.Builder classSpec = TypeSpec.classBuilder(ClassName.get(packageName, className).simpleName() + "ViewInjector")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.VIEWINJECTOR, classTypeName));

        // 2、构建成员变量
        FieldSpec.Builder targetSpec = FieldSpec.builder(classTypeName, "target")
                .addModifiers(Modifier.PRIVATE);
        classSpec.addField(targetSpec.build());


        // Map参数类型 Map<String,Integer> resIdMap
        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(Integer.class));

        // 3、构建方法  public void inject(T t,Object object,Finder finder)
        MethodSpec.Builder injectMethodSpec = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addAnnotation(TypeUtil.ANDROID_ANNOTATION_UITHREAD)
                .returns(TypeName.VOID)
                .addParameter(classTypeName, "object", Modifier.FINAL)// 入参为Activity、Fragment
                .addParameter(Object.class, "source")
                .addParameter(TypeUtil.FINDER, "finder");

        //  构建方法 public void unbind()
        MethodSpec.Builder unbindMethodSpec = MethodSpec.methodBuilder("unbind")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addAnnotation(TypeUtil.ANDROID_ANNOTATION_UITHREAD)
                .returns(TypeName.VOID);

        // 4、添加方法内容
        injectMethodSpec.addStatement("target = object");
        unbindMethodSpec.addStatement("if (target == null) throw new IllegalStateException(\"Bindings already cleared.\")");

        if (mFeild.size() > 0) {
            for (MyBindViewField field : mFeild) {
                String fieldName = field.getFieldName();
                TypeMirror fieldType = field.getFieldType();
                int resId = field.getResId(); // 获取主项目中的id值
                String idName = field.getIdName(); // 获取lib中的did值
                // host.textView = (TextView) host.findViewById(R.id.tv_msg)
                if (!"".equals(idName)) {
                    // 读取 com.viewinject.bindview.Id.class中的值
                    injectMethodSpec.addStatement("target.$N = ($T)finder.findView(source,$S)", fieldName, ClassName.get(fieldType), idName);
                } else {
                    injectMethodSpec.addStatement("target.$N = ($T)finder.findView(source,$L)", fieldName, ClassName.get(fieldType), resId);
                }
                unbindMethodSpec.addStatement("target.$N = null", fieldName);
            }
        }
        // 声明 Listener，构建内部类
        if (mMethod.size() > 0) {
            // 构建Listener内部实现类
            for (MyOnclickMethod method : mMethod) {
                String methodName = method.getMethodName();
                int resId = method.getResId();
                String idName = method.getIdName();
                MethodSpec methodSpec = MethodSpec.methodBuilder("onClick")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .addParameter(TypeUtil.ANDROID_VIEW, "view")
                        .returns(TypeName.VOID)
                        .addStatement("object.$N()", methodName)
                        .build();
                TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(TypeUtil.ANDROID_ONCLICK_LISTENER)
                        .addMethod(methodSpec)
                        .build();
                // 构建成员变量
                FieldSpec.Builder fieldSpec = FieldSpec.builder(TypeUtil.ANDROID_VIEW, methodName)
                        .addModifiers(Modifier.PRIVATE);
                classSpec.addField(fieldSpec.build());
                if (!"".equals(idName)) {
                    injectMethodSpec.addStatement("this.$N = finder.findView(source,$S)", methodName, idName);
                } else {
                    injectMethodSpec.addStatement("this.$N = finder.findView(source,$L)", methodName, resId);
                }
                injectMethodSpec.addStatement("this.$N.setOnClickListener($L)", methodName, listener);
                unbindMethodSpec.addStatement("this.$N.setOnClickListener(null)", methodName);
                unbindMethodSpec.addStatement("this.$N = null", methodName);
            }
        }
        unbindMethodSpec.addStatement("target = null");

        // 5、构建类信息
        classSpec.addMethod(injectMethodSpec.build())
                .addMethod(unbindMethodSpec.build());
        return JavaFile.builder(packageName, classSpec.build()).build();
    }

    // 包名
    public String getPackageName() {
        return mElementUtils.getPackageOf(typeElement).getQualifiedName().toString();
    }

    // 类名
    public String getClassName() {
        return typeElement.getSimpleName().toString();
    }

    // 获取类的绝对路径
    public String getFullClassName() {
        return typeElement.getQualifiedName().toString();
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

}
