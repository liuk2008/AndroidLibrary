package com.android.database.config;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 注意：SQLiteOpenHelper类的 getWritableDatabase 方法底层实际上调用的是Context的openOrCreateDatabase方法，而这个方法是不支持带路径的数据库名称的，
 * 创建的数据库只能放在/data/data/包名称/ 目录下，另外还需要申请SD卡读写权限
 * <p>
 * 更改数据库文件路径为SD卡
 * 1、继承ContextWrapper类，复写openOrCreateDatabase方法
 * 2、复写getDatabasePath方法，创建数据库路径
 */
public class DatabaseContext extends ContextWrapper {

    private static final String TAG = "DatabaseContext";
    private File file; // SD卡上APP文件路径

    public DatabaseContext(Context context) {
        super(context);
    }

    public DatabaseContext(Context context, File file) {
        super(context);
        this.file = file;
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return result;
    }

    @Override
    public File getDatabasePath(String name) {
        // 判断是否存在sd卡
        boolean sdExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!sdExist) {//如果不存在,
            Log.d(TAG, "SD卡不存在，请加载SD卡");
            return null;
        }

        // 数据库所在目录
        if (file == null) {
            file = new File(Environment.getExternalStorageDirectory() + "/common/db");
        }
        String dbDir = file.getAbsolutePath();
        String dbPath = dbDir + "/" + name;//数据库路径

        //判断目录是否存在，不存在则创建该目录
        File dirFile = new File(dbDir);
        if (!dirFile.exists())
            dirFile.mkdirs();

        //数据库文件是否创建成功
        boolean isFileCreateSuccess = false;
        //判断文件是否存在，不存在则创建该文件
        File dbFile = new File(dbPath);
        if (!dbFile.exists()) {
            try {
                isFileCreateSuccess = dbFile.createNewFile();//创建文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            isFileCreateSuccess = true;
        }

        //返回数据库文件对象
        if (isFileCreateSuccess)
            return dbFile;
        else
            return null;
    }

}