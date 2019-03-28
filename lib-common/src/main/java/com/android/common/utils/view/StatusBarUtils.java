package com.android.common.utils.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

public class StatusBarUtils {

    /**
     * 设置状态栏颜色
     */
    public static void configStatusBarBgColor(final Activity activity, final int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            window.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    View statusBarView = getStatusBarView(activity);
                    statusBarView.setBackgroundColor(color);
                    window.getDecorView().removeOnLayoutChangeListener(this);
                }
            });
        }
    }


    /**
     * 设置状态栏颜色
     */
    public static void configStatusBarBgRes(final Activity activity, final int bgResource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            window.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    View statusBarView = getStatusBarView(activity);
                    statusBarView.setBackgroundResource(bgResource);
                    window.getDecorView().removeOnLayoutChangeListener(this);
                }
            });
        }
    }

    /**
     * 设置状态栏透明
     */
    public static void configStatusBar(@NonNull Activity activity) {
        Window window = activity.getWindow();
        View contentContainerView = activity.findViewById(Window.ID_ANDROID_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            contentContainerView.setPadding(0, getStatusBarHeight(), 0, 0);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor("#33000000"));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            contentContainerView.setPadding(0, getStatusBarHeight(), 0, 0);
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(@NonNull Activity activity) {
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    /**
     * 获取状态栏View
     */
    public static View getStatusBarView(@NonNull Activity activity) {
        int identifier = activity.getResources().getIdentifier("statusBarBackground", "id", "android");
        View statusBarView = activity.getWindow().findViewById(identifier);
        return statusBarView;
    }

}
