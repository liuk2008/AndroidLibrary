package com.viewject.processor;


import com.viewinject.annotation.MyBindView;
import com.viewinject.annotation.MyOnClick;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

/**
 * 被MyOnclickMethon注解标记的模型类
 */
public class MyOnclickMethod {

    private ExecutableElement mElement;
    private int resId;
    private String idName;

    public MyOnclickMethod(Element element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.METHOD) // 此元素的种类
            throw new IllegalArgumentException(
                    String.format("Only field can be annotated with @%s",
                            MyOnclickMethod.class.getSimpleName()));
        mElement = (ExecutableElement) element;
        // 获取指定注解元素的值
        MyOnClick bindView = mElement.getAnnotation(MyOnClick.class);
        resId = bindView.value();
        idName = bindView.resId();
        if (resId < 0 && "".equals(idName))
            throw new IllegalArgumentException(
                    String.format("value() in %s for field % is not valid",
                            MyBindView.class.getSimpleName(), mElement.getSimpleName()));
    }

    public int getResId() { // 获取注解上的值
        return resId;
    }

    public String getIdName() {
        return idName;
    }

    public String getMethodName() {
        return mElement.getSimpleName().toString();  // 返回此元素的简单名称
    }

    public TypeMirror getFieldType() {
        return mElement.asType(); // 返回此元素定义的类型
    }
}
