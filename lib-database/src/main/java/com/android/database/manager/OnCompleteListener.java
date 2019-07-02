package com.android.database.manager;

/**
 * Created by admin on 2019/6/26
 */
public interface OnCompleteListener {

    int SUCCESS = 0;
    int FAIL = 1;

    void onQueryComplete(int status);

    void onUpdateComplete(int status);

    void onInsertComplete(int status);

    void onDeleteComplete(int status);

}
