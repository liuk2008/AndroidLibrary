# AndroidDemo

Android演示项目

**自定义Android Gradle插件**

    * 1、在工程下新建一个module，名字必须为buildSrc或buildsrc，系统默认识别此目录
         * 1、在main目录下，新建groovy目录，在该文件下建立以.groovy结尾的文件。
         * 2、在main目录下，新建resources/META-INF/gradle-plugins目录，在该文件夹下创建一个以.properties结尾的文件，
              文件名是我们要引用的插件名称，文件内容：implementation-class=xxxxx（对应groovy目录下插件具体实现类）
    * 2、配置build.gradle内容：
         apply plugin: 'groovy'

         dependencies {
             //gradle sdk
             compile gradleApi()
             //groovy sdk
             compile localGroovy()
         }

    * 3、引用方式：
         * 1、引用以.properties结尾的文件名称：apply plugin:'com.xx.plugin'
         * 2、引用插件全类名：apply plugin:com.xx.plugin.XXXPlugin
