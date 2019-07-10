package com.example.myjava.reference;

import java.util.HashMap;
import java.util.HashSet;

/**
 * HashSet集合底层默认元素不能重复
 * 1、HashSet底层是通过HashMap实现的，Key值为add进去的元素，Value值为当前HashSet对象
 * 2、HashMap底层调用put()方法添加元素时，会判断对象元素的hashCode是否相等，及调用对象的equals()方法
 * e.hash == hash && key.equals(e.key)
 * 3、添加对象元素时，复写equals()方法，未复写hashCode()方法时
 * equals返回true，而hashCode不一致，则不符合Java相等的对象必须具有相等的散列码（hashCode）
 * equals方法是根据对象的特征值复写
 * Created by Administrator on 2018/5/16.
 */

public class EqualsDemo {

    public static void equalsHashSet() {
        StudentDemo demo1 = new StudentDemo(1, "test1");
        StudentDemo demo2 = new StudentDemo(1, "test1");
        System.out.println(demo1.equals(demo2));
        HashSet<StudentDemo> hashSet = new HashSet<>();
        hashSet.add(demo1);
        hashSet.add(demo2);
        System.out.println("hashSet:" + hashSet);
    }

    public static void equalsHashMap() {
        StudentDemo demo1 = new StudentDemo(1, "test1");
        StudentDemo demo2 = new StudentDemo(1, "test1");
        System.out.println(demo1.equals(demo2));
        HashMap<StudentDemo, Integer> hashMap = new HashMap<>();
        hashMap.put(demo1, 1);
        hashMap.put(demo2, 2);
        System.out.println("hashMap:" + hashMap);
    }
}
