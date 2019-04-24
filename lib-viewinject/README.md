# AndroidLibrary #

## lib-viewinject ##

    注意：尽量避免各个lib包之间相互引用

**注解管理器机制**

    * 1、annotation：注解包，统一管理注解。包括：MyBindView、MyOnClick
    * 2、compiler：注解管理器包，在编译时扫描和处理注解，生成对应的Java文件，统一处理注解作用的元素
         MyBindViewField：处理被MyBindView注解标记的元素
         MyOnclickMethod：处理被MyOnclickMethod注解标记的元素
         AnnotatedClass：动态构建Java代码，实现ViewInjector接口，方法体中动态调用Class对象和Finder接口
         ViewInjectProcessor：处理扫描到的注解元素，创建AnnotatedClass对象，生成Java源文件
    * 3、bindview：通过反射调用APT生成的Java文件，实现控件id的查找
         ViewFinder：实现Finder接口，底层查找控件id
         ViewInjector：定义接口，在AnnotatedClass中调用
         MyViewInjector：
		 1、通过反射获取系统R文件中关于id的Class对象，同时创建ViewFinder对象
		 2、通过反射调用生成的Java源文件，入参Class对象和ViewFinder对象
    * 注意：
           1、使用此lib包时，会增加APK的方法数量以及APK的大小
           2、使用时注意内存泄漏
           3、Annotation中必须引用 final 的值，所以在lib中使用会出现异常
           4、在主项目中引用 apply plugin: com.android.plugin.GenerateClassPlugin，可解决 manifest packName 和 applicationId 不一致问题

    * 混淆规则
            -keep class com.viewinject.bindview.** { *; }
            -keep class **ViewInjector{ *; }
            -keepclasseswithmembernames class * {
                @MyBindView.* <fields>;
            }
            -keepclasseswithmembernames class * {
                @MyOnClick.* <methods>;
            }