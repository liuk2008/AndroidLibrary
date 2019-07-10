package com.example.android;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class NetTipView {

    private ViewGroup viewGroup;
    private Activity activity;
    private View mTipView;

    public NetTipView(Activity activity) {
        this.activity = activity;
    }

    public void initView() {
        if (viewGroup == null) {
            ViewGroup rootViewGroup = activity.findViewById(android.R.id.content);
            viewGroup = (ViewGroup) rootViewGroup.getChildAt(0);
        }
        if (mTipView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            mTipView = inflater.inflate(R.layout.view_network_tip, null);
        }
    }

    public void setTipView(boolean isConnected) {
        if (mTipView == null) return;
        if (isConnected)
            viewGroup.removeView(mTipView);
        else
            viewGroup.addView(mTipView, 0);
    }

}
