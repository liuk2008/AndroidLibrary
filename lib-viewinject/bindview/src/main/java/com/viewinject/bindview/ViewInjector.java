package com.viewinject.bindview;

import androidx.annotation.UiThread;

import com.viewinject.bindview.finder.Finder;

public interface ViewInjector<T> {

    @UiThread
    void inject(T t, Object source, Class<?> cls, Finder finder);

    @UiThread
    void unbind();
}
