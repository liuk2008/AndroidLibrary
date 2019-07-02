package com.android.database.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 创建数据库
 * Created by Administrator on 2017/5/2.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseOpenHelper.class.getSimpleName();
    private DatabaseConfig dataConfig;

    public DatabaseOpenHelper(Context context, DatabaseConfig dataConfig) {
        super(context, dataConfig.getDbName(), null, dataConfig.getDbVersion());
        this.dataConfig = dataConfig;
    }

    /**
     * 数据库创建时调用
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        DatabaseConfig.TableCreateListener listener = dataConfig.getTableCreateListener();
        if (listener == null) {
            throw new RuntimeException("未初始化 TableCreateListener");
        }
        Log.d(TAG, "onCreate: 创建数据库");
        listener.onCreate(db);
    }


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d(TAG, "onOpen: 打开数据库");
    }

    /**
     * 数据库升级时调用
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DatabaseConfig.TableUpgradeListener listener = dataConfig.getTableUpgradeListener();
        if (listener == null) {
            throw new RuntimeException("未初始化 TableUpgradeListener");
        }
        listener.onUpgrade(db, oldVersion, newVersion);
    }

}
