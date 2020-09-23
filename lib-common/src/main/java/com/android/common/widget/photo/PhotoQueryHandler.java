package com.android.common.widget.photo;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * 异步查询类
 *
 * @author liuk
 */
public class PhotoQueryHandler extends AsyncQueryHandler {

    private static final String TAG = "PhotoQueryHandler";
    private ArrayList<String> datas = new ArrayList<>(); // 图片路径
    private RecyclerView.Adapter adapter;

    public PhotoQueryHandler(ContentResolver cr, RecyclerView.Adapter adapter) {
        super(cr);
        this.adapter = adapter;
    }

    // 在主线程中调用，通过token区分当前查询的标识，cookie为查询数据
    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        if (cookie != null && cursor != null) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(1);
                datas.add(path); // path
            }
            cursor.close();
        }
        ((ArrayList<String>) cookie).clear();
        ((ArrayList<String>) cookie).addAll(datas);
        adapter.notifyDataSetChanged();
    }
}
