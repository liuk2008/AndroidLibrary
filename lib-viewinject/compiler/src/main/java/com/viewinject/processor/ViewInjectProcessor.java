package com.viewinject.processor;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.viewinject.annotation.MyBindView;
import com.viewinject.annotation.MyOnClick;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * 每一个注解处理器类都必须有一个空的构造函数，默认不写就行
 * <p>
 * APT(Annotation Processing Tool)是一种处理注释的工具,
 * 1、扫描源代码文件中指定的Annotation
 * 2、按照指定的规则对扫描到的Annotation进行处理，同时生成指定的源文件
 * 3、也可以编译生成的源文件和原来的源文件，将它们一起生成class文件
 * <p>
 * AutoService 主要的作用是注解 processor 类，替换在resources/META-INF/services/文件夹下生成Processor配置信息文件 javax.annotation.processing.Processor
 * JavaPoet 这个库的主要作用就是帮助我们通过类调用的形式来生成代码。
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.viewinject.annotation.MyBindView", "com.viewinject.annotation.MyOnClick"})
// 处理的注解全称
@SupportedSourceVersion(SourceVersion.RELEASE_8)    // 处理的JDK版本
public class ViewInjectProcessor extends AbstractProcessor {

    // 文件相关的辅助类
    private Filer mFiler;
    // 元素相关的辅助类
    private Elements mElementUtils;
    //  日志相关的辅助类
    private Messager mMessager;
    //  解析的目标注解集合
    private Map<String, AnnotatedClass> mAnnotatedClassMap = new HashMap<>();

    /**
     * init()方法会被注解处理工具调用，并输入ProcessingEnvironment参数。
     * ProcessingEnvironment提供很多有用的工具类Elements, Types 和 Filer
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        info("---------init--------");
    }

    /**
     * 这相当于每个处理器的主函数main()，你在这里写你的扫描、评估和处理注解的代码，以及生成Java文件。
     * 输入参数RoundEnvironment，可以让你查询出包含特定注解的被注解元素
     *
     * @param annotations 请求处理的注解类型
     * @param roundEnv    有关当前和以前的信息环境
     * @return 如果返回 true，则这些注解已声明并且不要求后续 Processor 处理它们；
     * 如果返回 false，则这些注解未声明并且可能要求后续 Processor 处理它们
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        info("---------process--------");
        // 1、扫描指定的注解元素
        try {
            processView(roundEnv);
        } catch (Exception e) {
            error(e.getMessage());
            return true;
        }
        // 2、生成Java文件
        if (roundEnv.processingOver() && mAnnotatedClassMap.size() != 0) {
            for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
                try {
                    info("generating file for %s", annotatedClass.getClassName());
                    // 注意：每个AnnotatedClass对象生成一个对应的Java文件，此文件中的代码并没有被执行
                    JavaFile javaFile = annotatedClass.generateFinder();
                    if (javaFile != null)
                        javaFile.writeTo(mFiler);
                } catch (Exception e) {
                    error(e.getMessage());
                    return true;
                }
            }
        }

        return true;
    }

    // 处理扫描到的注解元素
    private void processView(RoundEnvironment roundEnv) {
        // 1、扫描使用指定注解的元素--MyBindView
        Set<? extends Element> bindViewElements = roundEnv.getElementsAnnotatedWith(MyBindView.class);
        for (Element element : bindViewElements) {
            // 2、根据注解元素所对应的类名生成AnnotatedClass对象，统一管理一个类下所有的注解元素值
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            // 3、根据注解元素创建对应的变量值
            MyBindViewField myBindViewField = new MyBindViewField(element);
            // 4、添加到AnnotatedClass对象中统一管理
            annotatedClass.addField(myBindViewField);
        }
        // 2、扫描使用指定注解的元素--MyOnClick
        Set<? extends Element> onClickElements = roundEnv.getElementsAnnotatedWith(MyOnClick.class);
        for (Element element : onClickElements) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            MyOnclickMethod myOnclickMethod = new MyOnclickMethod(element);
            annotatedClass.addMethod(myOnclickMethod);
        }
    }

    /**
     * 创建 AnnotatedClass 对象
     * 1、一个类名对应一个AnnotatedClass对象
     * 2、统一管理同一个类下所有的注解元素
     */
    private AnnotatedClass getAnnotatedClass(Element element) {
        // TypeElement : 一个类或接口程序元素
        TypeElement typeElement = (TypeElement) element.getEnclosingElement(); // 返回封装此元素的最里层元素，也就是返回使用类的信息
        String className = typeElement.getSimpleName().toString(); // 获取使用注解的类名
        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(className);
        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClass(typeElement, mElementUtils,mMessager); // 一个类对应生成一个AnnotatedClass对象
            mAnnotatedClassMap.put(className, annotatedClass);
        }
        return annotatedClass;
    }

    // 指定JDK 版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return super.getSupportedSourceVersion();
    }

    // 获取处理的注解名称
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return super.getSupportedAnnotationTypes();
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}
