package com.viewinject.bindview.finder;

import androidx.annotation.UiThread;
import android.view.View;

public interface Finder {
    @UiThread
    View findView(Object object, int resId);

    @UiThread
    View findView(Object object, Class<?> cls, String idName);
}
