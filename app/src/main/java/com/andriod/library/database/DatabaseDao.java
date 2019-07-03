package com.andriod.library.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.database.config.DatabaseConfig;
import com.android.database.manager.DatabaseManager;

import java.util.List;

public class DatabaseDao {

    private static final String TAG = DatabaseDao.class.getSimpleName();
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
        List<UserInfo> list = dbManager.queryEntity("select * from userinfo", null, UserInfo.class);
        for (int i = 0; i < list.size(); i++) {
            UserInfo user = list.get(i);
            Log.d(TAG, "userInfo: " + user);
        }
    }

    public void testMsgInfo() {
        for (int i = 0; i < 3; i++) {
            dbManager.insert("insert into message(msg_title,msg_content,msg_status) values(?,?,?)",
                    new Object[]{"title" + i, "content" + i, i});
        }
        List<MsgInfo> list = dbManager.queryData("select * from message", null, MsgInfo.class);
        for (int i = 0; i < list.size(); i++) {
            MsgInfo msgInfo = list.get(i);
            Log.d(TAG, "msgInfo: " + msgInfo);
        }
    }

}
