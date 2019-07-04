package com.android.database.manager;

import android.database.Cursor;

import com.android.database.annotation.Column;
import com.android.database.annotation.Table;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2019/7/2
 */
public class TableEntity {

    private static final String TAG = TableEntity.class.getSimpleName();
    private static String tableName = "temp";

    /**
     * 创建表结构
     *
     * @param clazz 表结构实例Class对象
     * @return 返回sql语句
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
     * @param cursor
     * @param <T>
     */
    public static <T> void getTableEntity(T t, Cursor cursor) {
        try {
            Field[] fields = t.getClass().getDeclaredFields();
            for (Field field : fields) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    String column = annotation.name();
                    int columnIndex = cursor.getColumnIndex(column);
                    Class<?> type = field.getType();
                    if (type == Integer.TYPE) {
                        field.setInt(t, columnIndex != -1 ? cursor.getInt(columnIndex) : 0);
                    } else if (type == Long.TYPE)
                        field.setLong(t, columnIndex != -1 ? cursor.getLong(columnIndex) : 0);
                    else if (type == Float.TYPE)
                        field.setFloat(t, columnIndex != -1 ? cursor.getFloat(columnIndex) : 0.0f);
                    else if (type == Double.TYPE)
                        field.setDouble(t, columnIndex != -1 ? cursor.getDouble(columnIndex) : 0.0);
                    else if (type == Boolean.TYPE) { // 0 true 1 false
                        field.setBoolean(t, (columnIndex != -1) && cursor.getInt(columnIndex) == 0);
                    } else
                        field.set(t, columnIndex != -1 ? cursor.getString(columnIndex) : null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 转换sql语句
     *
     * @param object 实例对象
     * @return
     */

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
            for (Field field : fields) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    String column = annotation.name();
                    sql.append(column).append(",");
                    // 转换数据
                    Class<?> type = field.getType();
                    if (type == Integer.TYPE)
                        args.add(field.getInt(object));
                    else if (type == Long.TYPE)
                        args.add(field.getLong(object));
                    else if (type == Float.TYPE) {
                        float value = field.getFloat(object);
                        // 对Float类型的转换是直接强转Number类型，然后获取double值的，这样转换直接由四个字节转八个字节，补位会使的float数值精度丢失
                        args.add(Double.valueOf(String.valueOf(value)));
                    } else if (type == Double.TYPE)
                        args.add(field.getDouble(object));
                    else if (type == Boolean.TYPE) // 0 true 1 false
                        args.add(field.getBoolean(object) ? 0 : 1);
                    else
                        args.add(field.get(object));
                }
            }
            sql.replace(sql.length() - 1, sql.length(), ") ");
            sql.append("values(");
            int size = args.size();
            for (int i = 0; i < size; ++i) {
                if (i == size - 1)
                    sql.append("?)");
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

    /**
     * 转换类型
     *
     * @param clazz 实例Class对象
     */
    public static Type getWrapperType(final Class clazz) {
        Type wrapperType = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                Type[] types = new Type[1];
                types[0] = clazz;
                return types;
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return List.class;
            }
        };
        return wrapperType;
    }

}
