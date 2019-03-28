package com.android.common.utils.common;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Toast utilities
 *
 * @author Ryan
 */
public class ToastUtils {
    private static Toast toast;

    /**
     * Show toast by string message
     *
     * @param context
     * @param message
     */
    public static void showToast(Context context, String message) {
        if (context != null && !TextUtils.isEmpty(message))
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Show toast by resources id
     *
     * @param context
     * @param resourceId
     */
    public static void showToast(Context context, int resourceId) {
        if (context != null)
            Toast.makeText(context, context.getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 此方法可以不等上一个吐司消失的情况下连续弹吐司，
     *
     * @param msg
     */
    public static void getToast(Context context, String msg) {
        if (context != null) {
            if (toast == null) {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            }
            toast.setText(msg);
            toast.show();
        }
    }
}
