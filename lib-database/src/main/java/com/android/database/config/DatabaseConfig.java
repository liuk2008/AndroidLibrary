package com.android.database.config;

import android.database.sqlite.SQLiteDatabase;

import com.android.database.manager.TableEntity;

import java.io.File;

/**
 * 数据库配置类
 * Created by liuk on 2019/6/3
 */
public class DatabaseConfig {

    private static final String TAG = DatabaseConfig.class.getSimpleName();
    private File sdFile;
    private int dbVersion = 1;
    private String dbName = "", tableSql = "";

    public DatabaseConfig setSDFile(File sdFile) {
        this.sdFile = sdFile;
        return this;
    }

    public DatabaseConfig setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public DatabaseConfig setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
        return this;
    }

    public File getSDFile() {
        return sdFile;
    }

    public String getDbName() {
        return dbName;
    }

    public int getDbVersion() {
        return dbVersion;
    }

    public String getTableSql() {
        return tableSql;
    }

    public DatabaseConfig setTable(Class<?> clazz) {
        if (clazz == null) {
            return this;
        }
        tableSql = TableEntity.getTableColumn(clazz);
        return this;
    }

    // 设置数据库监听
    private DatabaseConfig.TableCreateListener tableCreateListener;
    private DatabaseConfig.TableUpgradeListener tableUpgradeListener;

    public DatabaseConfig setDbCreateListener(DatabaseConfig.TableCreateListener tableCreateListener) {
        this.tableCreateListener = tableCreateListener;
        return this;
    }

    public DatabaseConfig setDbUpgradeListener(DatabaseConfig.TableUpgradeListener tableUpgradeListener) {
        this.tableUpgradeListener = tableUpgradeListener;
        return this;
    }

    public DatabaseConfig.TableCreateListener getTableCreateListener() {
        return this.tableCreateListener;
    }

    public DatabaseConfig.TableUpgradeListener getTableUpgradeListener() {
        return this.tableUpgradeListener;
    }

    public interface TableCreateListener {
        void onCreate(SQLiteDatabase db);
    }

    public interface TableUpgradeListener {
        void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
    }

}
