package com.android.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by liuk on 2019/3/27
 */
class InjectClassPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        Logger.project = project
        Logger.error("====== 引入 " + InjectClassPlugin.name + " 插件 ======")
        def classTransform = new InjectClassTransform(project)
        project.android.registerTransform(classTransform)
        // 自定义Extension，Gradle脚本中通过Extension传递一些配置参数给自定义插件
        def extension = project.extensions.create("injectClass", InjectClassExtension)
        project.afterEvaluate { // project 配置完成后回调
            Logger.error("====== 获取 extension 配置参数 ======")
            Logger.error(extension.injectDirClass)
            Logger.error(extension.injectDirCode)
            Logger.error(extension.injectJarClass)
            Logger.error(extension.injectJarCode)
            classTransform.injectDirClass = extension.injectDirClass
            classTransform.injectDirCode = extension.injectDirCode
            classTransform.injectJarClass = extension.injectJarClass
            classTransform.injectJarCode = extension.injectJarCode
        }
    }
}

class InjectClassExtension {
    String injectDirClass
    String injectDirCode
    String injectJarClass
    String injectJarCode
}
