# AndroidLibrary

**lib-common**

     注意：尽量避免各个lib包之间相互引用

      * h5包
          * 1、封装自定义WebView，设置WebSettings
          * 2、封装WebViewUtils工具类，提供获取Cookie，Header等方法
          * 3、封装WebChromeClient，提供showPicWindow回调方法，处理文件上传
          * 4、封装WebViewClient，提供相关回调方法
               1、onLoadUrl：处理特定URL
               2、executorJs：与js脚本交互
               3、处理网络错误，显示自定义错误界面
          * 5、封装WebViewHelper，加载自定义WebView，监听对应事件，提供方法
               1、getFromAssets：从assets中加载文件
               2、injectJs、excJsMethod、evaluateJs、addJavascriptInterface：与js相关的方法
               3、showPicWindow、setWebViewPic：调用本地拍照、图库等功能，向WebView传递文件
               4、增加js文件，注入到h5页面，关联本地方法与js方法，实现点击图片放大、缩小、长按等功能
          * 注意：使用WebView时存在内存泄漏问题

      * refreshview包
          * 1、增加自定义RefreshView：
               1、布局文件使用SwipeRefreshLayout与RecyclerView嵌套，初始化view
               2、通过监听SwipeRefreshLayout刷新事件，提供回调接口实现下拉刷新功能
               3、通过监听RecyclerView滑动事件，提供回调接口实现上拉加载功能
               4、对外提供方法可设置下拉刷新、上拉加载、重新刷新功能是否开启，默认开启
               5、自定义Loading页面、加载失败页面、无数据页面，对外提供方法可设置/显示对应状态的View
               6、设置Adapter，底层通过MyCommonAdapter填充数据，提供item点击、添加数据功能
          * 2、增加自定义MyCommonAdapter，继承于RecyclerView.Adapter
               1、设置Adapter，提供item点击、添加数据功能
               2、包装原始Adapter，统一添加底部布局，根据数据量动态展示
               3、封装RecyclerView.Adapter公共方法，通过MyViewHolder封装itemView
               4、提供convert方法填充item数据

      * utils包
          * 1、common：一般功能工具类
          * 2、exception：异常工具类
          * 3、safety：安全校验类
          * 4、system：系统工具类
          * 5、view：view相关工具类
          * 注意：使用ShakeInfoUtil工具类可以获取当前页面结构

     * widget包
          * photo包：
            * 1、增加PhotoDialogActivity，此Activity为透明模式，通过拍照或系统图库获取图片
            * 2、增加PhotoPickerActivity，遍历系统图库展示图片列表，可选择对应图片
          * view包：
            * 1、CircleImageView：设置原型头像
            * 2、PasswordView：设置支付密码
          * PermissionActivity，此Activity为Dialog形式，用于Android 6.0 动态申请权限
            * 1、可单独使用工具类请求权限，获取未授权的权限信息
            * 2、申请单个、多个权限时，弹出对话框，进入到应用系统信息页面




