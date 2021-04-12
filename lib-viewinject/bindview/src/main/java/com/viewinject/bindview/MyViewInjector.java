package com.viewinject.bindview;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import android.view.View;

import com.viewinject.bindview.finder.ViewFinder;

import java.util.HashMap;
import java.util.Map;

public class MyViewInjector {

    private static final String TAG = "MyViewInjector";

    private static final Map<String, ViewInjector> injectorMap = new HashMap<>();

    private static ViewFinder viewFinder = new ViewFinder();

    public static void bindView(Activity activity) {
        bind(activity, activity);
    }

    public static void bindView(Fragment fragment, View view) {
        bind(fragment, view);
    }

    public static void unbindView(Activity activity) {
        unbind(activity);
    }

    public static void unbindView(Fragment fragment) {
        unbind(fragment);
    }

    private static void bind(Object target, Object source) {
        try {
            String className = target.getClass().getName();
            ViewInjector injector = injectorMap.get(className);
            if (injector == null) {
                String proxyClassName = className + "ViewInjector";
                Class<?> clazz = Class.forName(proxyClassName);
                injector = (ViewInjector) clazz.newInstance();
                injectorMap.put(className, injector);
            }
            injector.inject(target, source, viewFinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void unbind(Object target) {
        String className = target.getClass().getName();
        try {
            ViewInjector injector = injectorMap.get(className);
            if (injector != null) {
                injector.unbind();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
