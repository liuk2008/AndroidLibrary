package com.android.plugin

import com.android.utils.FileUtils
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import org.gradle.api.Project

/**
 * Created by liuk on 2019/3/27
 */
class GenerateClass {

    // 初始化类池
    private final static ClassPool pool = ClassPool.getDefault()

    static void generateId(Project project, String packageName, String path) {
        // 将当前路径加入类池,不然找不到这个类
        pool.appendClassPath(path)

        // 加入android.jar，不然找不到android相关的所有类
//        String androidJarPath = project.android.bootClasspath[0].toString()
//        pool.appendClassPath(androidJarPath)

        // 加载 R.class 文件
        String className = packageName + ".R"
        CtClass rCtClass = pool.getCtClass(className)
        // 获取 R 文件中的内部类
        CtClass[] rCtClasses = rCtClass.getNestedClasses()

        // 修改class文件时，需要解冻
//        if (ctClass.isFrozen()) ctClass.defrost()

        // 删除旧文件，创建新class
        String idPath = path + "\\com\\viewinject\\bindview\\Id.class"
        File file = new File(idPath)
        FileUtils.deleteIfExists(file)
        CtClass idClass = pool.makeClass("com.viewinject.bindview.Id")

        rCtClasses.each { CtClass innerClass ->
            if ((className + "\$id").equals(innerClass.name)) {
                project.logger.error("====== 扫描 " + innerClass.name + " 文件 ======")
                project.logger.error("R.class path : " + (path + "\\" + packageName.replace(".", "\\")))
                CtField[] ctFields = innerClass.getDeclaredFields()
                ctFields.each { CtField ctField ->
                    String name = ctField.name
                    int value = ctField.constantValue
                    String field = "  public static final int " + name + " = " + value + ";"
                    CtField idField = CtField.make(field, idClass)
                    idClass.addField(idField)
                    project.logger.error("id : " + name + " --> " + value)
                }
                project.logger.error("====== 共计" + ctFields.length + "个id ======")
            }
            innerClass.detach()
        }
        project.logger.error("====== 生成 com.viewject.bindview.Id.class 文件 ======")
        /**
         * 如果一个 CtClass 对象通过 writeFile(), toClass(), toBytecode() 被转换成一个类文件，
         * 此 CtClass 对象会被冻结起来，不允许再修改。因为一个类只能被 JVM 加载一次。
         */
        idClass.writeFile(path)
        // 从ClassPool中释放，避免内存溢出
        rCtClass.detach()
        idClass.detach()
    }

}

