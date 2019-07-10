package com.example.myjava.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

/**
 * Java对象运行
 * 1、通过new关键字创建一个对象后，编译生成class文件
 * 2、类加载器将class文件加载到JVM内存中，生成Class对象
 * 3、Class对象是在加载类时由Java虚拟机以及通过调用类加载器中的defineClass
 * 方法自动构造的。一个类只产生一个Class对象
 * 4、jvm创建对象前，会先检查类是否加载，寻找类对应的class对象，
 * 若加载好，则为你的对象分配内存，初始化也就是代码:new Object()。
 * <p>
 * <p>
 * 反射的本质：获取类的class文件对象后，反向获取对象中的属性信息
 * 反射的核心：JVM在运行时才动态加载类或调用方法/访问属性，它不需要事先（写代码的时候或编译期）知道运行对象是谁。
 * <p>
 * 通过反射创建对象：1、通过Class对象的newInstance创建对应类的实例  2、通过Class对象获取指定的Constructor对像创建
 * 通过反射运行配置文件的内容
 * 通过反射越过泛型检查,泛型在编译时有效，在运行时会跳过检查
 * <p>
 * 动态代理，通过反射生成一个代理对象
 * <p>
 * Created by liuk on 2018/3/6 0006.
 */

public class ReflectTest {

    public static void reflect() throws Exception {
        //      LogUtils.logd(TAG, LogUtils.getThreadName() + ":" + Integer.toHexString(hashCode()));
        AdminDaoImpl adminDao = new AdminDaoImpl();

        // 1、4 种方式获取类的字节码文件对象
        // AdminDao.class;
        // adminDao.getClass();
        // Class.forName("AdminDaoImpl"); // 优先考虑
//        ClassLoader.getSystemClassLoader().loadClass("com.freestyle.java.reflect.AdminDaoImpl");
        Class clazz = Class.forName("com.freestyle.myjava.reflect.AdminDaoImpl");

        // 2、获取成员变量
        System.out.println("====获取公共成员变量，包括父类=====");
        Field[] fields = clazz.getFields(); // 获取所有public权限的变量，包括其父类的公用变量
        for (Field field : fields) {
            System.out.println("field:" + field.getName());
        }
        System.out.println("====获取所有成员变量，不包括父类=====");
        Field[] declaredFields = clazz.getDeclaredFields(); // 获取所有已声明的成员变量。但不能得到其父类的成员变量
//        declaredFields = Field.class.getDeclaredFields();
        for (Field declareField : declaredFields) {
//            Class<?> type = declareField.getType();  // 获取成员变量类型
//            System.out.println("type:" + type.getName());
            System.out.println("declareField:" + declareField.getName());
        }

        // 3、获取成员方法
        System.out.println("====获取公共成员方法，包括父类=====");
        Method[] methods = clazz.getMethods(); // 获取所有公用（public）方法包括其父类的公用方法
        for (Method method : methods) {
            System.out.println("method:" + method.getName());
        }
        System.out.println("====获取所有成员方法，不包括父类======");
        Method[] declaredMethods = clazz.getDeclaredMethods(); // 获取返回类或接口声明的所有方法，不包括继承的方法。
//        declaredMethods = Method.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println("declaredMethod:" + declaredMethod.getName());
        }

        // 4、通过反射机制赋值
        System.out.println("====通过反射机制赋值======");
        Field name = clazz.getDeclaredField("name");
        // 暴力破解
        name.setAccessible(true);
        System.out.println("name:" + name.get(adminDao));  // 获取成员变量默认值
        name.set(adminDao, "测试");
        System.out.println("adminDao:" + adminDao);

        System.out.println("====通过反射调用私有方法======");
        Method setName = clazz.getDeclaredMethod("setName", String.class);
        setName.setAccessible(true);
        setName.invoke(adminDao, "测试");

        // 5、通过反射机制创建对象
        System.out.println("====通过反射创建对象======");
        Constructor constructor = clazz.getConstructor();
        Object ad1 = constructor.newInstance();
        Object ad2 = clazz.newInstance();
        System.out.println("ad1:" + ad1);
        System.out.println("ad2:" + ad2);
    }

    // InvocationHandler代理对象
    public static void testProxy() {
        AdminDao adminDao1 = new AdminDaoImpl();
        adminDao1.register();
        adminDao1.login();
        System.out.println("-------使用代理对象-----");
        // 添加代理对象
        MyInvocationHandler my = new MyInvocationHandler(adminDao1);
        AdminDao adminDao2 = (AdminDao) Proxy.newProxyInstance(adminDao1.getClass().getClassLoader(),
                adminDao1.getClass().getInterfaces(), my);
        adminDao2.register();
        adminDao2.login();
    }

    public static void test() {
        System.out.println(Integer.TYPE == int.class ? true : false);
    }

    // ============== 1、数组参数类型============
    public static void testFunc1() throws Exception {
        ReflectTest test = new ReflectTest();
        Class clazz = test.getClass();
        Method func = clazz.getDeclaredMethod("func1", String[].class);
        func.setAccessible(true);
        // 注意：可变参数，这个变量其实是数组，它会自动把多个参数组装成一个数组
        func.invoke(test, new String[1]);// 运行正常：null，将可变参数拆解，长度为1，但是没有元素，所以null
        func.invoke(test, (Object) new String[]{"a", "b"}); // 运行正常：2
        func.invoke(test, new Object[]{new String[]{"a", "b", "c"}}); // 运行正常：3
//        func.invoke(test, new String[]{"a"}); // 报错：将可变参数拆解后，入参为String-->a，与func1入参String[]类型不符合
//        func.invoke(test, new String[]{"a", "b"}); // 报错：将可变参数拆解后，入参个数为2个，与func1入参个数为1不符合
//        func.invoke(test, new String[2]);// 报错：同上
    }

    public static void testFunc2() throws Exception {
        ReflectTest test = new ReflectTest();
        Class clazz = test.getClass();
        Method func = clazz.getDeclaredMethod("func2", String.class, String[].class);
        func.setAccessible(true);
        String str = new String();
        // 注意：可变参数，这个变量其实是数组，它会自动把多个参数组装成一个数组
        func.invoke(test, str, new String[1]); // 运行正常：1
        func.invoke(test, str, (Object) new String[]{"a", "b"}); // 运行正常：2
//        func.invoke(test, str, new Object[]{new String[]{"a", "b", "c"}}); // 报错：入参为Object[]类型，与func2入参String[]类型不符合
        func.invoke(test, str, new String[]{"a"}); // 运行正常：1
        func.invoke(test, str, new String[]{"a", "b"}); // 运行正常：2
        func.invoke(test, str, new String[2]);// 运行正常：2
    }

    private void func1(String[] args) {
        System.out.println(args == null ? "null" : args.length);
    }

    private void func2(String key, String[] args) {
        System.out.println(args == null ? "null" : args.length);
    }

    private static final Integer INTEGER_VALUE = 100;
    private static final int INT_VALUE = 100;
    private static final String STR = "test";
    private static final String STRING = new String("test");


    // ============== 2、通过反射修改常量值============
    public static void testFinal() throws Exception {
        ReflectTest test = new ReflectTest();
        Class clazz = test.getClass();
        Field field1 = clazz.getDeclaredField("INTEGER_VALUE");
        Field field2 = clazz.getDeclaredField("INT_VALUE");
        Field field3 = clazz.getDeclaredField("STR");
        Field field4 = clazz.getDeclaredField("STRING");
        field1.setAccessible(true);
        field2.setAccessible(true);
        field3.setAccessible(true);
        field4.setAccessible(true);

        /*
         * 注意：
         * 1、每个对象的成员变量在反射时会生成一个Field对象
         * 2、Field 对象有个一个属性叫做 modifiers,它表示的是属性是否是 public, private, static, final 等修饰的组合因此，
         * 可以获取每个成员变量的修饰符，两种方式
         *    1、通过获取Field对象modifiers成员变量的值：Modifier.toString((int)modifiersField.get(field1))
         *    2、通过调用Field对象的getModifiers()方法获取：Modifier.toString(field1.getModifiers())
         */
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);

        /*
         * 注意：
         * 1、用static final 修饰的成员变量，通过反射调用Field对象get()方法获取值后，再调用Field对象
         * set()方法修改值，会抛出异常 IllegalAccessException
         * 2、只用 static 或者 final 修饰的成员变量，调用get()方法后再调用set()方法修改值，运行正常
         */
//        System.out.println("modifiersField:" + field1.get(test));
//        System.out.println("modifiersField:" + field2.getInt(test));

        //  用按位取反 ~ 再按位与，~ 操作把 final 从修饰集中剔除掉，其他特性如 private, static 保持不变。
        modifiersField.setInt(field1, field1.getModifiers() & ~Modifier.FINAL);
        modifiersField.setInt(field2, field2.getModifiers() & ~Modifier.FINAL);
        modifiersField.setInt(field3, field3.getModifiers() & ~Modifier.FINAL);
        modifiersField.setInt(field4, field4.getModifiers() & ~Modifier.FINAL);
        System.out.println("=====修改后=====");
        field1.set(test, 200);
        field2.set(test, 300);
        field3.set(test, "demo");
        field4.set(test, "demo");
        /*
         * 注意：
         * 1、final修饰非引用类型常量时，JAVA在编译的时候就会把代码中对此常量中引用的地方替换成相应常量值，
         * 比如 System.out.println(100)。但是通过get()方法获取对应常量值时，则会输出修改后的值。
         * 2、final修饰引用类型常量时，通过反射修改常量值，则会输出修改后的值
         */
        System.out.println("INTEGER_VALUE:" + INTEGER_VALUE); // 200
        System.out.println("INT_VALUE:" + INT_VALUE); // 100
        System.out.println("STR:" + STR); // test
        System.out.println("STR:" + STRING); // demo
        System.out.println("modifiersField:" + field1.get(test)); // 200
        System.out.println("modifiersField:" + field2.getInt(test)); // 300
        System.out.println("modifiersField:" + field3.get(test)); // demo
        System.out.println("modifiersField:" + field4.get(test)); // demo
    }

    // ============== 3、通过反射获取内部类============
    public static void testInner() throws Exception {
        Class outerClazz = ReflectTest.class;
        Object outer = outerClazz.newInstance();
        // 调用外部类方法
        Method outerMethod = outerClazz.getDeclaredMethod("outer");
        outerMethod.invoke(outer);
        // 调用内部类方法
        Class[] innerClazzs = outerClazz.getDeclaredClasses();
        for (Class innerClazz : innerClazzs) {
//            int modifiers = innerClazz.getModifiers();
//            String name = Modifier.toString(modifiers);
            String className = innerClazz.getSimpleName();
            switch (className) {
                case "InnerA":
                    // 成员内部类持有外部类的引用，构造函数需要入参外部类引用
                    Constructor constructorA = innerClazz.getDeclaredConstructor(outerClazz, String.class);
                    Method innerA = innerClazz.getDeclaredMethod("innerA");
                    innerA.invoke(constructorA.newInstance(outer, "innerA"));
                    break;
                case "InnerB":
                    // 静态内部类不持有外部类的引用，所以构造函数不需要入参外部类
                    Constructor constructorB = innerClazz.getDeclaredConstructor(String.class);
                    Method innerB = innerClazz.getDeclaredMethod("innerB");
                    innerB.invoke(constructorB.newInstance("innerB"));
                    break;
                case "InnerC":
                    Constructor constructorC = innerClazz.getDeclaredConstructor(outerClazz);
                    constructorC.setAccessible(true); // 未显式复写构造函数，需设置为可以获取的
                    Method innerC = innerClazz.getDeclaredMethod("innerC");
                    innerC.invoke(constructorC.newInstance(outer));
                    break;
                default:
                    break;
            }
        }
        // 调用匿名内部类
        Field r = outerClazz.getDeclaredField("r");
        Runnable runnable = (Runnable) r.get(outerClazz.newInstance());
        runnable.run();
    }


    public void outer() {
        System.out.println("Outer class");
    }

    private class InnerA {
        private String name;

        public InnerA(String name) {
            InnerA.this.name = name;
        }

        public void innerA() {
            System.out.println(name + " class");
        }
    }

    private static class InnerB {
        private String name;

        public InnerB(String name) {
            InnerB.this.name = name;
        }

        public void innerB() {
            System.out.println(name + " class");
        }
    }

    private class InnerC {
        public void innerC() {
            System.out.println("InnerC class");
        }
    }

    // 匿名内部类中是不能定义构造函数的，能存在任何的静态成员变量和静态方法。
    private Runnable r = new Thread() {
        @Override
        public void run() {
            System.out.println("Method run of Runnable r");
        }
    };

}
