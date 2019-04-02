# AndroidLibrary

**lib-network**

    注意：尽量避免各个lib包之间相互引用

    * 1、封装网络框架：
        * 1、Retrofit2 + CallBack：封装Retrofit网络框架
             增加Cookie管理机制、设置请求头
        * 2、Retrofit2 + RxJava2：封装Retrofit+RxJava2网络框架
            增加Retrofit缓存机制、Cookie管理机制、设置请求头、日志机制、重新连接机制
        * 3、Http + Callback：封装原生网路框架
        * 4、每个网络框架可取消单个请求，也可取消全部请求
        * 5、网络层统一检测网络连接状态，包含：网络未连接，网络已连接但无法正常访问
        * 6、release版本设置NO_PROXY，禁止通过代理抓取http、https请求
    * 2、ErrorHandler：处理网络请求异常
    * 3、Null：当网络请求正常但无返回数据时，可使用Null对象解析

    * http请求场景
    * 1、网络层200情况下
        |--1、业务层存在数据
        |-----1、业务层数据格式标准
        |--------1、业务层200，处理返回数据
        |-----------1、存在数据，使用数据model解析
        |-----------2、不存在数据，使用Null对象解析
        |--------2、业务层非200，抛出 ErrorException，通过ErrorHandler处理
        |-----2、业务层数据非标准格式，使用数据model解析
        |--2、业务层不存在数据，使用Null对象解析
    * 2、网络层非200情况下
        |--1、网络异常时，捕获异常，通过ErrorHandler处理
        |--2、网络正常，业务层异常通过网络层抛出时，通过ErrorHandler处理

    * 注意：
        * 1、页面销毁后取消网络回调
        * 2、使用过程中注意内存泄漏问题
