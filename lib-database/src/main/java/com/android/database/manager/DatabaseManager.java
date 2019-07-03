package com.android.database.manager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.database.config.DatabaseConfig;
import com.android.database.config.DatabaseContext;
import com.android.database.config.DatabaseOpenHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 数据库查询类
 */
public class DatabaseManager {

    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final String QUERY = "query";
    private static final String INSERT = "insert";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";

    private static DatabaseManager dbManager;
    private DatabaseOpenHelper openHelper;
    private SQLiteDatabase database;
    private ExecutorService executor;

    private DatabaseManager(Context context, DatabaseConfig dbConfig) {
        // 创建数据库
        DatabaseContext dbContext = new DatabaseContext(context);
        openHelper = new DatabaseOpenHelper(dbContext, dbConfig);
        executor = Executors.newCachedThreadPool();
        database = openHelper.getReadableDatabase();
    }

    public static DatabaseManager getInstance(Context context, DatabaseConfig dbConfig) {
        if (null == dbManager)
            dbManager = new DatabaseManager(context, dbConfig);
        return dbManager;
    }


    //============================操作数据库============================

    /**
     * 查询数据
     * 使用实例对象创建表结构时使用此方法查询数据
     */
    public <T> List<T> queryEntity(final String sql, final String[] args, final Class<T> clazz) {
        List<T> data = new ArrayList<>();
        int status = OnCompleteListener.FAIL;
        Future<List<T>> future = executor.submit(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                // 注意开启事务
                Log.d(TAG, "-->开始数据查询");
                List<T> list = new ArrayList<>();
                Cursor cursor = null;
                try {
                    cursor = database.rawQuery(sql, args);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            T t = clazz.newInstance();
                            for (int i = 1; i < cursor.getColumnCount(); i++) {
                                int type = cursor.getType(i);
                                String name = cursor.getColumnName(i);
                                if (type == Cursor.FIELD_TYPE_INTEGER) {
                                    int value = cursor.getInt(i);
                                    TableEntity.getTableEntity(t, name, value);
                                } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                                    float value = cursor.getFloat(i);
                                    TableEntity.getTableEntity(t, name, value);
                                } else {
                                    String value = cursor.getString(i);
                                    TableEntity.getTableEntity(t, name, value);
                                }
                            }
                            list.add(t);
                        }
                    }
                    Log.d(TAG, "--> 完成数据查询");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                }
                return list;
            }
        });
        try {
            List<T> list = future.get();
            Log.d(TAG, "--> query successful");
            data.addAll(list); // 返回查询的数据
            status = OnCompleteListener.SUCCESS;
        } catch (Exception e) {
            Log.d(TAG, "--> query fail");
            e.printStackTrace();
        }
        showStatus(QUERY, status);
        return data;
    }

    /**
     * 查询数据
     * 使用sql创建表结构时使用此方法查询数据
     */
    public <T> List<T> queryData(final String sql, final String[] args, Class<T> clazz) {
        List<Map<String, Object>> data = new ArrayList<>();
        int status = OnCompleteListener.FAIL;
        Future<List<Map<String, Object>>> future = executor.submit(new Callable<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> call() {
                // 注意开启事务
                Log.d(TAG, "-->开始数据查询");
                List<Map<String, Object>> list = new ArrayList<>();
                Cursor cursor = null;
                try {
                    cursor = database.rawQuery(sql, args);
                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            for (int i = 1; i < cursor.getColumnCount(); i++) {
                                int type = cursor.getType(i);
                                String name = cursor.getColumnName(i);
                                if (type == Cursor.FIELD_TYPE_INTEGER) {
                                    int value = cursor.getInt(i);
                                    hashMap.put(name, value);
                                } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                                    float value = cursor.getFloat(i);
                                    hashMap.put(name, value);
                                } else {
                                    String value = cursor.getString(i);
                                    hashMap.put(name, value);
                                }
                            }
                            list.add(hashMap);
                        }
                    }
                    Log.d(TAG, "--> 完成数据查询");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                        cursor = null;
                    }
                }
                return list;
            }
        });
        try {
            List<Map<String, Object>> list = future.get();
            Log.d(TAG, "--> query successful");
            data.addAll(list); // 返回查询的数据
            status = OnCompleteListener.SUCCESS;
        } catch (Exception e) {
            Log.d(TAG, "--> query fail");
            e.printStackTrace();
        }
        showStatus(QUERY, status);
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String json = gson.toJson(data);
        Type type = TableEntity.getWrapperType(clazz);
        return gson.fromJson(json, type);
    }

    /**
     * 插入数据
     *
     * @param object 实例对象
     */
    public void insert(Object object) {
        Object[] sqlInfo = TableEntity.insertTableEntity(object);
        String sql = (String) sqlInfo[0];
        Object[] args = (Object[]) sqlInfo[1];
        insert(sql, args);
    }

    /**
     * 插入数据
     *
     * @param sql  sql语句
     * @param args 查询参数
     */
    public void insert(String sql, Object[] args) {
        execSql(sql, args, UPDATE);
    }

    /**
     * 更新数据
     *
     * @param sql
     * @param args 查询参数
     */
    public void update(String sql, Object[] args) {
        execSql(sql, args, UPDATE);
    }

    /**
     * 删除语句
     *
     * @param sql  sql 语句
     * @param args 查询参数
     */
    public void delete(String sql, Object[] args) {
        execSql(sql, args, DELETE);
    }

    private void execSql(final String sql, final Object[] args, String type) {
        Future<Object> future = executor.submit(new Callable<Object>() {
            @Override
            public Object call() {
                try {
                    // 执行sql成功
                    database.execSQL(sql, args);
                    return new Object();
                } catch (Exception e) {
                    // 执行sql失败
                    e.printStackTrace();
                    return null;
                }
            }
        });
        try {
            Object object = future.get();
            Log.d(TAG, "--> " + type + " " + (null != object ? "successful" : "fail"));
            int status = (null != object ? OnCompleteListener.SUCCESS : OnCompleteListener.FAIL);
            showStatus(type, status);
        } catch (Exception e) {
            Log.d(TAG, "--> " + type + " fail");
            showStatus(type, OnCompleteListener.FAIL);
            e.printStackTrace();
        }
    }

    // 执行回调方法
    private void showStatus(String type, int status) {
        if (null != onCompleteListener) {
            if (QUERY.equals(type)) {
                onCompleteListener.onQueryComplete(status);
            } else if (INSERT.equals(type)) {
                onCompleteListener.onInsertComplete(status);
            } else if (UPDATE.equals(type)) {
                onCompleteListener.onUpdateComplete(status);
            } else if (DELETE.equals(type)) {
                onCompleteListener.onDeleteComplete(status);
            }
        }
    }

    public void open() {
        Log.d(TAG, "open: " + database.isOpen());
        // 打开数据库
        if (null != database && !database.isOpen())
            database = openHelper.getReadableDatabase();
    }

    public void close() {
        if (null != database)
            database.close();
    }

    private OnCompleteListener onCompleteListener;

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }


}
