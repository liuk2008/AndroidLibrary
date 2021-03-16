package com.example.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.mvp.RequestManager;


public abstract class BaseActivity extends AppCompatActivity {

    private RequestManager requestManager = new RequestManager();
    public boolean isCheckNetwork = true;
    private NetTipView tipView;  // 默认每个页面都检测网络
    private NetReceiver netReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        if (isCheckNetwork) {
            netReceiver = new NetReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netReceiver, filter);
            tipView = new NetTipView(this);
            tipView.initView(); // 初始化view
        }
    }


    // 退出页面时，取消请求回调
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isCheckNetwork) {
            unregisterReceiver(netReceiver);
            netReceiver = null;
            tipView = null;
        }
        requestManager.cancelAll();
        requestManager = null;
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tipView.setTipView(checkNetwork(context));
        }
    }

    private boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();//  获取可用网络
            if (null != info && info.isConnected()) {
                return info.getState() == NetworkInfo.State.CONNECTED;
            }
        }
        return false;
    }


}
