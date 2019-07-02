package com.android.database.manager;

import com.android.database.annotation.Column;
import com.android.database.annotation.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2019/7/2
 */
public class TableEntity {

    private static String tableName = "temp";

    /**
     * 创建表结构
     *
     * @param clazz 表结构实例对象
     */
    public static String getTableColumn(Class<?> clazz) {
        //  获取表名称
        Table tableAnnotation = (Table) clazz.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            tableName = tableAnnotation.name();
        }
        // 获取表结构字段名称
        StringBuilder keys = new StringBuilder();
        StringBuilder columns = new StringBuilder();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null) {
                Class<?> type = field.getType();
                String column = annotation.name(); // 字段名称
                boolean primaryKey = annotation.primaryKey(); // 是否是主键
                if (primaryKey) {
                    keys.append(column + ",");
                }
                // 组装Sql字段
                if (type == Integer.TYPE || type == Long.TYPE) {
                    columns.append(column + " integer,");
                } else if (type == Float.TYPE || type == Double.TYPE) {
                    columns.append(column + " real,");
                } else if (type == Boolean.TYPE) {
                    columns.append(column + " integer,");
                } else {
                    columns.append(column + " text,");
                }
            }
        }
        if (keys.length() == 0)
            // 自定义主键
            return "create table " + tableName + "(id integer primary key autoincrement," + columns.substring(0, columns.length() - 1) + ")";
        else
            return "create table " + tableName + "(id integer primary key autoincrement," + columns.toString() + "primary key(" + keys.substring(0, keys.length() - 1) + "))";
    }

    /**
     * 解析数据
     *
     * @param t      表结构实例对象
     * @param object
     * @param <T>
     */
    public static <T> void getTableEntity(T t, String name, Object object) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    String column = annotation.name();
                    if (name.equalsIgnoreCase(column)) {
                        Class<?> type = field.getType();
                        if (type == Integer.TYPE || type == Long.TYPE) {
                            long value = (object == null ? 0 : (long) object);
                            field.setLong(t, value);
                        } else if (type == Float.TYPE || type == Double.TYPE) {
                            double value = (object == null ? 0.0 : (double) object);
                            field.setDouble(t, value);
                        } else if (type == Boolean.TYPE) { // 0 true 1 false
                            int value = (object == null ? 1 : (int) object);
                            field.setBoolean(t, value == 0);
                        } else {
                            String value = (object == null ? "" : (String) object);
                            field.set(t, value);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //  insert into message (msg_title,msg_content,msg_status) values(?,?,?), new Object[]{title, content, i}
    public static Object[] insertTableEntity(Object object) {
        Object[] sqlInfo = new Object[2];
        try {
            StringBuilder sql = new StringBuilder();
            Class<?> clazz = object.getClass();
            List<Object> args = new ArrayList<>();
            sql.append("insert into ");
            sql.append(tableName);
            sql.append("(");
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    String column = annotation.name();
                    sql.append(column);
                    if (i == fields.length - 1)
                        sql.append(")");
                    else
                        sql.append(",");
                    // 转换数据
                    Class<?> type = field.getType();
                    if (type == Integer.TYPE)
                        args.add(field.getInt(object));
                    else if (type == Long.TYPE)
                        args.add(field.getLong(object));
                    else if (type == Float.TYPE)
                        args.add(field.getFloat(object));
                    else if (type == Double.TYPE)
                        args.add(field.getDouble(object));
                    else if (type == Boolean.TYPE) // 0 true 1 false
                        args.add(field.getBoolean(object) ? 0 : 1);
                    else
                        args.add(field.get(object));
                }
            }
            sql.append(" values(");
            int size = args.size();
            for (int i = 0; i < size; ++i) {
                if (i == size - 1)
                    sql.append(")");
                else
                    sql.append("?,");
            }
            sqlInfo[0] = sql.toString();
            sqlInfo[1] = args.toArray(new Object[size]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlInfo;
    }


}
