package com.example.myjava.collections;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 1、创建一个ArrayList集合，添加元素
 * 2、创建一个AbstractSet的实现类，并将ArrayList中的迭代器Iterator传入
 * 3、在AbstractSet实现类中重写iterator()方法，底层创建一个Iterator对象并返回
 * 4、重写Iterator对象的next()、hasNext()等方法，底层实际上调用ArrayList中的迭代器Iterator的next()、hasNext()方法
 * Created by Administrator on 2018/5/17.
 */

public class HashSetDemo {

    public void testHashSet() {
        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        list.add("test3");
        list.add("test3");
        System.out.println("list:" + list);
        Iterator<String> iterator = list.iterator();
        MyHashSet hashSet = new MyHashSet(iterator);
        System.out.println("hashSet:" + hashSet);
        System.out.println("hashSet size:" + hashSet.size());
    }

    class MyHashSet extends AbstractSet<String> {
        private Iterator<String> iterator;
        private int size = 0;

        public MyHashSet(Iterator<String> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public String next() {
                    size++;
                    return iterator.next();
                }

                @Override
                public void remove() {
                    iterator.remove();
                }
            };
            // set持有ArrayList的Iterator，但是多次打印的值不一致，
//            return iterator;
        }

        @Override
        public int size() {
            return size;
        }
    }

}
