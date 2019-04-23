package com.viewinject.processor;

import com.squareup.javapoet.ClassName;

public class TypeUtil {
    public static final ClassName FINDER = ClassName.get("com.viewinject.bindview.finder", "Finder");
    public static final ClassName VIEWINJECTOR = ClassName.get("com.viewinject.bindview", "ViewInjector");
    public static final ClassName ANDROID_ONCLICK_LISTENER = ClassName.get("android.view", "View", "OnClickListener");
    public static final ClassName ANDROID_VIEW = ClassName.get("android.view", "View");
    public static final ClassName ANDROID_ANNOTATION_UITHREAD = ClassName.get("android.support.annotation", "UiThread");
}
