package com.android.common.refreshview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by liuk on 2018/8/20 0020.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = MyViewHolder.class.getSimpleName();
    private View mConvertView;
    private SparseArray<View> mViews;

    private MyViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        this.mViews = new SparseArray<>();
    }

    public static MyViewHolder getHolder(View itemView) {
        MyViewHolder myHolder = (MyViewHolder) itemView.getTag();
        if (myHolder == null) {
            myHolder = new MyViewHolder(itemView);
            itemView.setTag(myHolder);
        }
        return myHolder;
    }

    public View getConvertView() {
        return mConvertView;
    }


    public <T extends View> T getView(int viewId) {
        View view = this.mViews.get(viewId);
        if (view == null) {
            view = this.mConvertView.findViewById(viewId);
            this.mViews.put(viewId, view);
        }

        return (T) view;
    }

    public MyViewHolder setText(int viewId, String text) {
        TextView tv = this.getView(viewId);
        tv.setText(text);
        return this;
    }

    public MyViewHolder setImageResource(int viewId, int resId) {
        ImageView view = this.getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    public MyViewHolder setVisible(int viewId, boolean visible) {
        View view = this.getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }


    public MyViewHolder setOnClickListener(int viewId, OnClickListener listener) {
        View view = this.getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public MyViewHolder setOnTouchListener(int viewId, OnTouchListener listener) {
        View view = this.getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public MyViewHolder setOnLongClickListener(int viewId, OnLongClickListener listener) {
        View view = this.getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

}
