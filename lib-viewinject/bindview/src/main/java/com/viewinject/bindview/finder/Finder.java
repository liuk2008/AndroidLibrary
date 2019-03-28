package com.viewinject.bindview.finder;

import android.support.annotation.UiThread;
import android.view.View;

public interface Finder {
    @UiThread
    View findView(Object object, int resId);
}
