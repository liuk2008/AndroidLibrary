package com.viewinject.bindview.finder;

import android.app.Activity;
import android.support.annotation.UiThread;
import android.view.View;

public class ViewFinder implements Finder {

    @UiThread
    @Override
    public View findView(Object source, int resId) {
        if (source instanceof Activity) {
            return ((Activity) source).findViewById(resId);
        } else {
            return ((View) source).findViewById(resId);
        }
    }

    @UiThread
    @Override
    public View findView(Object source, Class<?> cls, String idName) {
        try {
            int resId = (int) cls.getField(idName).get(cls);
            return findView(source, resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
