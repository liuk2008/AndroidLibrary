package com.viewinject.bindview;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.viewinject.bindview.finder.ViewFinder;

import java.util.HashMap;
import java.util.Map;

public class MyViewInjector {

    private static final String TAG = MyViewInjector.class.getSimpleName();

    private static final Map<String, ViewInjector> injectorMap = new HashMap<>();

    private static ViewFinder viewFinder = new ViewFinder();
    private static Class cls = null;

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
            if (cls == null) {
                if (target instanceof Activity) {
                    Activity activity = (Activity) target;
                    cls = Class.forName(activity.getPackageName() + ".R$id");
                } else if (target instanceof Fragment) {
                    Fragment fragment = (Fragment) target;
                    cls = Class.forName(fragment.getContext().getPackageName() + ".R$id");
                }
            }
            String className = target.getClass().getName();
            ViewInjector injector = injectorMap.get(className);
            if (injector == null) {
                String proxyClassName = className + "ViewInjector";
                Class<?> clazz = Class.forName(proxyClassName);
                injector = (ViewInjector) clazz.newInstance();
                injectorMap.put(className, injector);
            }
            injector.inject(target, source, cls, viewFinder);
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
