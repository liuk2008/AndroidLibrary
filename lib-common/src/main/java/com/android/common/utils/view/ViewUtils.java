package com.android.common.utils.view;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class ViewUtils {

    private static void setViewStatus(View view, int type) {
        if (null != view && view.getVisibility() != type)
            view.setVisibility(type);
    }

    /**
     * View相关方法
     */
    public static void setVisible(@NonNull View... views) {
        for (View view : views) {
            setViewStatus(view, View.VISIBLE);
        }
    }

    public static void setViewVisible(@NonNull View rootView, @NonNull int... ids) {
        for (int id : ids) {
            View view = rootView.findViewById(id);
            setViewStatus(view, View.VISIBLE);
        }
    }

    public static void setInvisible(@NonNull View... views) {
        for (View view : views) {
            setViewStatus(view, View.INVISIBLE);
        }
    }

    public static void setViewInvisible(@NonNull View rootView, @NonNull int... ids) {
        for (int id : ids) {
            View view = rootView.findViewById(id);
            setViewStatus(view, View.INVISIBLE);
        }
    }

    public static void setGone(@NonNull View... views) {
        for (View view : views) {
            setViewStatus(view, View.GONE);
        }
    }

    public static void setViewGone(@NonNull View rootView, @NonNull int... ids) {
        for (int id : ids) {
            View view = rootView.findViewById(id);
            setViewStatus(view, View.GONE);
        }
    }

    public static void setText(TextView textView, @Nullable Object text) {
        if (null != textView && null != text) {
            if (text instanceof CharSequence)
                textView.setText((CharSequence) text);
            else
                textView.setText(String.valueOf(text));
        }
    }

    public static void setViewText(@NonNull View rooView, @IdRes int textViewId, @Nullable Object text) {
        TextView textView = rooView.findViewById(textViewId);
        setText(textView, text);
    }


    public static void setOnClickListener(View.OnClickListener listener, View... views) {
        for (View view : views) {
            view.setOnClickListener(listener);
        }
    }

    public static void setViewOnClickListener(View.OnClickListener listener, View rootView, int... ids) {
        for (int id : ids) {
            View view = rootView.findViewById(id);
            if (null != view)
                view.setOnClickListener(listener);
        }
    }

}
