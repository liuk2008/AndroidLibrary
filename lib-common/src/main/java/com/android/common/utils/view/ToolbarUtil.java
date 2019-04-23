package com.android.common.utils.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.common.R;


public class ToolbarUtil {

    private static final String TAG = ToolbarUtil.class.getSimpleName();
    private static final int BACKGROUND_RES = 0;
    private static final int BACKGROUND_COLOR = 1;
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_BACK = 1;

    /**
     * 设置背景色
     */
    public static void configTitleBgRes(Activity activity, int res) {
        View rootView = activity.findViewById(android.R.id.content);
        configTitleBgRes(rootView, res);
    }

    public static void configTitleBgColor(Activity activity, int color) {
        View rootView = activity.findViewById(android.R.id.content);
        configTitleBgColor(rootView, color);
    }

    public static void configTitleBgRes(View rootView, int res) {
        configBackground(rootView, res, BACKGROUND_RES);
    }

    public static void configTitleBgColor(View rootView, int color) {
        configBackground(rootView, color, BACKGROUND_COLOR);
    }

    private static void configBackground(View rootView, int bgRes, int type) {
        if (rootView == null) return;
        View titleView = rootView.findViewById(R.id.view_title);
        if (titleView == null) return;
        if (type == BACKGROUND_RES) {
            titleView.setBackgroundResource(bgRes);
        } else if (type == BACKGROUND_COLOR) {
            titleView.setBackgroundColor(bgRes);
        }
    }

    /**
     * 设置标题
     */
    public static void configTitle(Activity activity, String title, int type) {
        View rootView = activity.findViewById(android.R.id.content);
        configTitlebar(rootView, title, type, TYPE_NORMAL);
    }

    public static void configTitle(View rootView, String title, int type) {
        configTitlebar(rootView, title, type, TYPE_NORMAL);
    }

    public static void configTitleBack(View rootView, String title) {
        configTitlebar(rootView, title, View.VISIBLE, TYPE_BACK);
    }

    private static void configTitlebar(View rootView, String title, int visible, final int type) {
        if (rootView == null) return;
        TextView tvTitle = rootView.findViewById(R.id.tv_title);
        ImageView ivArrow = rootView.findViewById(R.id.iv_arrow);
        if (tvTitle == null || ivArrow == null) return;
        if (!TextUtils.isEmpty(title)) tvTitle.setText(title);
        if (visible == View.VISIBLE || visible == View.INVISIBLE || visible == View.GONE)
            ivArrow.setVisibility(visible);
        ivArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getRootView().getContext();
                if (type == TYPE_NORMAL) {
                    if (context instanceof Activity) ((Activity) context).finish();
                    else {
                        context = v.getContext();
                        if (context instanceof Activity) ((Activity) context).finish();
                    }
                } else if (type == TYPE_BACK) {
                    if (context instanceof Activity) ((Activity) context).onBackPressed();
                    else {
                        context = v.getContext();
                        if (context instanceof Activity) ((Activity) context).onBackPressed();
                    }
                }
            }
        });

    }

}
