package com.android.plugin

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by liuk on 2019/3/27
 */
class GenerateClassPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def variants = project.android.applicationVariants
        project.logger.error("====== 引入 " + GenerateClassPlugin.name + " 插件 ======")
        // 注册一个Transform
        project.logger.error("====== 注册 " + GenerateClassTransform.name + " ======")
        def classTransform = new GenerateClassTransform(project)
        project.android.registerTransform(classTransform)
        project.afterEvaluate { // project 配置完成后回调
            variants.all { BaseVariant variant ->
                def packageName = variant.generateBuildConfig.buildConfigPackageName
                classTransform.packageName = packageName
//                variant.outputs.each { BaseVariantOutput output ->
//                    output.processResources.doLast {
//                        def srcDir = output.processResources.sourceOutputDir.path
//                        project.logger.error("========= srcPath :" + srcDir)
//                    }
//                }
            }
        }

    }
}
