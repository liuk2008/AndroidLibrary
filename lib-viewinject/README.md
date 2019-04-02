# AndroidLibrary

**viewinject**

    注意：尽量避免各个lib包之间相互引用

    * lib-viewinject注解管理器机制
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
        * 1、使用此lib包时，会增加APK的方法数量以及APK的大小
        * 2、使用时注意内存泄漏
        * 3、Annotation中必须引用 final 的值，所以在lib中使用会出现异常
        * 4、使用此lib包时，packName需和applicationId一致


    * R文件
        * 1、Android主项目R文件中控件id是常量，lib包中则不是常量
             Android在编译APK时会将lib包代码引入，为了防止主项目与lib包中的控件id值一样造成冲突，所以lib包中
             的控件id不是常量，方便Android编译时进行修改。

        * 2、Android主项目与lib包同时生成R文件，但是里面的控件id值不同
             Android在不同的项目中都生成R文件，但在运行时控件id全部从主项目中的R文件取值

        * 3、Android中不同的布局文件中控件id可以一致
             因为在Android的框架设计中，每一个控件都隶属于一棵控件树，每个控件都被其父控件所管理与调配，
             而根控件是一个容器控件，所有的子控件都是构造在这个根控件之上，这样并形成了一个控件树的控件域，
             在这个控件域中是不允许重名的，超出了这个控件域则这些控件的ID是无效的，也就是说在容器控件中的
             子控件是不允许重名的，而不在同一容器控件中的两个控件重名也无所谓。
