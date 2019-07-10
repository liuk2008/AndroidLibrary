package com.example.myjava.reference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 引用类型
 * 强引用、软引用、弱引用、虚引用，引用队列
 * Created by Administrator on 2018/5/15.
 */

public class ReferenceDemo {

    // 强引用，一般中断强引用和某个对象之间的关联，可以显示地将引用赋值为null
    public void strongReference() {
        Object object = new Object();
        String str = new String("hello");
    }

    // 软引用，只有在内存不足的时候JVM才会回收该对象。
    public static void softReference() {
        String str = new String("hello"); // 强引用
        SoftReference<String> softReference = new SoftReference<>(str);
        str = null; // 中断强引用，保证可以正常回收对象
        System.out.println("str:" + softReference.get());
        System.gc();
        System.out.println("str:" + softReference.get());
    }

    // 弱引用，gc运行后JVM会回收该对象
    public static void weakReference() {
        // 被弱引用关联的对象是指只有弱引用与之关联
        WeakReference<String> weakReference = new WeakReference<>(new String("hello"));
        System.out.println("gc前，str:" + weakReference.get());
        System.gc();
        System.out.println("gc后，str:" + weakReference.get()); // gc后，关联的对象被回收，显示null

        // 如果存在强引用同时与之关联，则进行垃圾回收时也不会回收该对象
        String str = new String("world"); // 强引用
        WeakReference<String> weakReference1 = new WeakReference<>(str);
        str = null; // 在gc前中断强引用，可以保证gc后正常回收关联的对象
        System.out.println("gc前，str:" + weakReference1.get());
        System.gc();
        System.out.println("gc后，str:" + weakReference1.get());
    }


    // 虚引用
    public void phantomReference() {

    }

    public static void testReference() {
        String str1 = new String("hello");
        String str2 = new String("world");
        ReferenceQueue<String> queue = new ReferenceQueue<>();
        SoftReference<String> softReference = new SoftReference<>(str1, queue);
        WeakReference<String> weakReference = new WeakReference<>(str2, queue);
//        WeakReference<String> weakReference = new WeakReference<>(str2);
        str1 = null;
        str2 = null;
        /*
         * 1、WeakReference所指向的对象被gc回收，则在ReferenceQueue里放置对应的WeakReference对象，未被gc回收，reference为null
         * 2、softReference 引用不受gc机制的影响，只有在内存不足时所指向的对象才会被回收。
         */
        System.gc();
        Reference<? extends String> reference = queue.poll(); // ReferenceQueue 只存在 WeakReference 引用
        if (reference != null) {
            System.out.println("gc后，weakReference:" + reference);
            System.out.println("gc后，str2:" + reference.get());
        } else {
            System.out.println("gc后，str1:" + softReference.get());
            System.out.println("gc后，str2:" + weakReference.get());
        }
    }

    /**
     * HashMap：gc后，元素依然存在
     * WeakHashMap：gc后，元素不存在，使用场景
     */
    public static void testWeakHashMap() {
        Map<StringBuffer, String> weakHashMap = new WeakHashMap<>();
        Map<StringBuffer, String> hashMap = new HashMap<>();
        StringBuffer str1 = new StringBuffer("hello");
        StringBuffer str2 = new StringBuffer("WeakHashMap");
        StringBuffer s1 = new StringBuffer("hello");
        StringBuffer s2 = new StringBuffer("HashMap");
        weakHashMap.put(str1, "test1");
        weakHashMap.put(str2, "test2");
        hashMap.put(s1, "test2");
        hashMap.put(s2, "test2");
        System.out.println("weakHashMap:" + weakHashMap);
        System.out.println("hashMap:" + hashMap);
        str1 = null;
        str2 = null;
        s1 = null;
        s2 = null;
        System.gc();
        System.out.println("weakHashMap:" + weakHashMap);
        System.out.println("hashMap:" + hashMap);
    }

    // 软引用使用方法
    private void demo() {
        Object object = new Object();
        SoftReference<Object> softReference = new SoftReference<>(object);
        object = null;
        if (softReference != null) {
            object = softReference.get();
        } else {
            object = new Object();
            softReference = new SoftReference<>(object);
        }
    }

}
