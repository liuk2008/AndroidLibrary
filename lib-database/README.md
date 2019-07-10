# AndroidLibrary #

## lib-database ##

    注意：尽量避免各个lib包之间相互引用

**database工具包**

    * 1、annotation：注解包，统一管理注解。包括：Table、Column
    * 2、config：
         DatabaseContext：设置数据库文件路径为SD卡
         DatabaseOpenHelper：数据库文件创建和升级
         DatabaseConfig：配置数据库基础信息，包括数据库名称、版本号、执行数据创建等
    * 3、manager：
         DatabaseManager：数据库查询类，底层异步执行Sql语句
         1、打开或关闭数据
         2、完成数据增、删、改、查
         3、执行数据库回调方法，返回sql语句执行状态
         TableEntity：
         1、根据实例对象创建表结构
         2、根据实例对象解析数据库数据
         3、将实例对象转换为Sql语句
    * 注意：使用此lib包时，需申请SD卡读写权限