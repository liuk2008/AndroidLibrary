package com.android.library.database;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import com.android.database.config.DatabaseConfig;
import com.android.database.manager.DatabaseManager;
import java.util.List;


public class DatabaseDao {

    private static final String TAG = "DatabaseDao";
    private static DatabaseDao instance;
    private DatabaseManager dbManager;

    private DatabaseConfig dataConfig = new DatabaseConfig()     // 初始化数据库配置文集
            .setDbName("data.db")
            .setDbVersion(1)
            .setTable(UserInfo.class)
            .setDbCreateListener(new DatabaseConfig.TableCreateListener() {
                @Override
                public void onCreate(SQLiteDatabase db) {
                    String userInfoSql = dataConfig.getTableSql();
                    String mgsSql = "create table message(_id integer primary key autoincrement," +
                            "msg_title text," +
                            "msg_content text," +
                            "msg_status integer," +
                            "msg_create_time not null default(datetime('now','localtime')))";
                    Log.d(TAG, "userInfoSql: " + userInfoSql);
                    Log.d(TAG, "mgsSql: " + mgsSql);
                    db.execSQL(userInfoSql);
                    db.execSQL(mgsSql);
                }
            });
//            .setDbUpgradeListener(new DatabaseConfig.TableUpgradeListener() {
//                @Override
//                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//                    Log.d(TAG, "onUpgrade: oldVersion:" + oldVersion);
//                    Log.d(TAG, "onUpgrade: newVersion:" + newVersion);
//                    if (oldVersion == newVersion) {
//                        return;
//                    }
//                    // 实现跨版本升级数据库
//                    switch (oldVersion) {
//                        case 1:
//                            // message 表新增1个字段
//                            String sql = "alter table message add column msg_type text";
//                            db.execSQL(sql);
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            });


    private DatabaseDao() {
    }

    public static synchronized DatabaseDao getInstance() {
        if (instance == null) {
            instance = new DatabaseDao();
        }
        return instance;
    }

    public void init(Context context) {
        dbManager = DatabaseManager.getInstance(context, dataConfig);
        dbManager.open();
    }

    public void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
            if (ActivityCompat.checkSelfPermission(activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    public void testUserInfo() {
        for (int i = 0; i < 3; i++) {
            UserInfo userInfo = new UserInfo();
            userInfo.name = "test" + i;
            userInfo.age = 20 + i;
            userInfo.phone = 18909131172L + i;
            userInfo.account = 1000.353f + i;
            userInfo.remain = 20135.65301 + i;
            if (i == 2) {
                userInfo.isDelete = true;
            }
            dbManager.insert(userInfo);
        }
        int count = dbManager.queryCount("userinfo");
        Log.d(TAG, "userinfo count: " + count);

        List<UserInfo> list = dbManager.queryEntity("select * from userinfo", null, UserInfo.class);
        for (int i = 0; i < count; i++) {
            UserInfo user = list.get(i);
            Log.d(TAG, "userInfo: " + user);
        }
    }

    public void testMsgInfo() {
        for (int i = 0; i < 3; i++) {
            dbManager.insert("insert into message(msg_title,msg_content,msg_status) values(?,?,?)",
                    new Object[]{"title" + i, "content" + i, i});
        }
        int count = dbManager.queryCount("message");
        Log.d(TAG, "message count: " + count);

        List<MsgInfo> list = dbManager.queryData("select msg_title from message", null, MsgInfo.class);
        Log.d(TAG, "message list: " + list.size());
        for (int i = 0; i < count; i++) {
            MsgInfo msgInfo = list.get(i);
            Log.d(TAG, "msgInfo: " + msgInfo);
        }
    }

}
