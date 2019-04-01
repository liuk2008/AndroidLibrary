package com.viewject.processor;


import com.viewinject.annotation.MyBindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * 被MyBindView注解标记模型类
 */
public class MyBindViewField {

    private VariableElement mElement;
    private int resId;
    private String idName;

    public MyBindViewField(Element element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.FIELD) // 此元素的种类
            throw new IllegalArgumentException(
                    String.format("Only field can be annotated with @%s",
                            MyBindView.class.getSimpleName()));
        mElement = (VariableElement) element;
        // 获取指定注解元素的值
        MyBindView bindView = mElement.getAnnotation(MyBindView.class);
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

    public String getFieldName() {
        return mElement.getSimpleName().toString();  // 返回此元素的简单名称
    }

    public TypeMirror getFieldType() {
        return mElement.asType(); // 返回此元素定义的类型
    }
}
