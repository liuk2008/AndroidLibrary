package com.android.common.utils.system;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class ClassUtils {

    /**
     * 获取成员变量的值
     */
    public static HashMap<String, Object> getFieldsValue(Object obj) {
        HashMap<String, Object> allField = new HashMap<>();
        try {
            Class<?> c = obj.getClass();
            Field[] fs = c.getDeclaredFields();
            for (Field field : fs) {
                String fieldName = field.getName();
                // 得到成员变量的值
                field.setAccessible(true);
                Object value = field.get(obj);
                if (null != value) {
                    allField.put(fieldName, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allField;
    }

    /**
     * 获取成员变量的名称
     */
    public static ArrayList<String> getFieldsName(Object obj) {
        ArrayList<String> allField = new ArrayList<>();
        try {
            Class<?> c = obj.getClass();
            Field[] fs = c.getDeclaredFields();
            for (Field field : fs) {
                // 得到成员变量的名称
                String fieldName = field.getName();
                allField.add(fieldName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allField;
    }

}
