# AndroidLibrary #

**lib包**

    * 1、lib-common：公共模块
         * 功能：1、WebView框架，2、自定义刷新View，3、工具类，4、widget包
    * 2、lib-database：数据库工具
         * 功能：1、创建数据库，2、创建表结构，3、完成数据增、删、改、查
    * 3、lib-network：网络框架
         * 1、Retrofit，RxJava2，Http三种网络框架
         * 2、兼容处理部分Http请求使用场景
    * 4、lib-scan：扫码模块
         * 1、使用摄像头识别二维码数据
         * 2、识别图片中二维码数据
    * 5、lib-viewinject：View注解工具
         * 1、annotation：注解包，统一管理注解。包括：MyBindView、MyOnClick
         * 2、compiler：注解管理器包，在编译时扫描和处理注解，生成对应的Java文件，统一处理注解作用的元素
         * 3、bindview：通过反射调用APT生成的Java文件，实现控件id的查找

**plugin插件**

    * 1、plugin-injectclass：
         * 功能：用于修改主项目或jar中指定的class文件
    * 2、plugin-viewinject：
         * 功能：动态生成Id.class文件，解决viewinject注解工具无法在lib包使用问题
