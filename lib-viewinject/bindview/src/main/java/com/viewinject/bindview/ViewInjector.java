package com.viewinject.bindview;

import android.support.annotation.UiThread;

import com.viewinject.bindview.finder.Finder;

public interface ViewInjector<T> {

    @UiThread
    void inject(T t, Object source, Finder finder);

    @UiThread
    void unbind();
}
