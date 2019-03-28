# AndroidLibrary

**viewinject**

    * viewinject注解管理器机制
         * 1、view-annotation：注解包，统一管理注解。包括：MyBindView、MyOnClick
         * 2、view-compiler：注解管理器包，在编译时扫描和处理注解，生成对应的Java文件，统一处理注解作用的元素
         * 3、view-bindview：MyViewInjector通过反射调用注解管理器生成的Java源文件
         * 注意：
               1、使用此lib包时，会增加APK的方法数量以及APK的大小
               2、使用注解时注意内存泄漏