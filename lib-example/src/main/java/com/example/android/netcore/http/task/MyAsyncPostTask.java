package com.example.android.netcore.http.task;

import android.os.AsyncTask;

import com.example.android.netcore.callback.NetCallback;
import com.example.android.netcore.callback.NetData;
import com.example.android.netcore.http.request.HttpPostRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.Map;


/**
 * Post 异步任务
 */
public class MyAsyncPostTask extends AsyncTask<Void, Void, String> {

    private String TAG = "MyAsyncPostTask";
    private HttpPostRequest httpRequest;
    private String url;
    private Map<String, Object> hashMap;
    private NetCallback callback;

    public MyAsyncPostTask(String url, Map<String, Object> hashMap, NetCallback callback) {
        httpRequest = HttpPostRequest.getInstance();
        this.url = url;
        this.hashMap = hashMap;
        this.callback = callback;
    }


    @Override
    protected String doInBackground(Void... voids) {
        if (!isCancelled()) {
            return httpRequest.doPost(url, hashMap);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);
        if (callback != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<NetData>() {
            }.getType();
            NetData data = gson.fromJson(response, type);
            if (data.getCode() == 200) {
                callback.netSuccess(data.getMsg());
            } else {
                callback.netFail(data.getCode(), data.getMsg());
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
