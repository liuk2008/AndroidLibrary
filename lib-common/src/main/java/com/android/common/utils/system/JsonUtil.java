package com.android.common.utils.system;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    /**
     * map 【转】 Json字符串
     */
    public static String parseMapToJson(Map<String, Object> map) {
        try {
            Gson gson = new Gson();
            return gson.toJson(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Json字符串 【转】 map
     */
    public static HashMap<String, Object> parseJsonToMap(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
        HashMap<String, Object> map = null;
        try {
            map = gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 对象 【转】 Json字符串
     */
    public static String parseBeanToJson(Object object) {
        Gson gson = new Gson();
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Json字符串 【转】 对象
     */
    public static <T> T parseJsonToBean(String json, Class<T> cls) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * list 【转】 Json字符串
     * list：存储元素为对象元素
     */
    public static String parseListToJson(List<Object> list) {
        Gson gson = new Gson();
        try {
            return gson.toJson(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Json字符串 【转】 list
     */
    public static <T> List<T> parseJsonToList(String json, TypeToken<T> typeToken) {
        Gson gson = new Gson();
        try {
            Type type = typeToken.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
     * 获取json串中某个字段的值，注意，只能获取同一层级的value
     *
     * @param json
     * @param key
     * @return
     */
    public static String getFieldValue(String json, String key) {
        if (TextUtils.isEmpty(json))
            return null;
        if (!json.contains(key))
            return "";
        JSONObject jsonObject = null;
        String value = null;
        try {
            jsonObject = new JSONObject(json);
            value = jsonObject.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return value;
    }

}
