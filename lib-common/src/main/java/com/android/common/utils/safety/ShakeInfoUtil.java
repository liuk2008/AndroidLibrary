package com.android.common.utils.safety;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.android.common.utils.exception.ExceptionManager;

import java.util.List;

/**
 */

public class ShakeInfoUtil {

    private static final String TAG = "ShakeInfoUtil";
    private static Activity currentActivity;
    private static boolean isInited;
    private static boolean isDialogShow;

    public static void init(final Application application) {
        try {
            // 防止多长初始化
            if (isInited) return;
            isInited = true;
            application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {
                    currentActivity = activity;
                }

                @Override
                public void onActivityPaused(Activity activity) {
                    if (currentActivity == activity)
                        currentActivity = null;
                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {

                }
            });
            // 获取 SensorManager 负责管理传感器
            /**
             * Android设备可能带有多种传感器，每种传感器的精度不同，当精度变换时onAccuracyChanged被触发，是sensor 指定了传感器的类型，而accuracy 为新的精度：
             *
             * SensorManager.SENSOR_STATUS_ACCURACY_HIGH  传感器报告高精度值
             * SensorManager.SENSOR_STATUS_ACCURACY_LOW  传感器报告低精度值
             * SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM  传感器报告平均精度值
             * SensorManager.SENSOR_STATUS_ACCURACY_UNRELIABLE  传感器报告的精度值不可靠。
             */
            SensorManager sensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (Math.abs(event.values[0]) + Math.abs(event.values[1]) > 30) {
                        if (null != currentActivity && !isDialogShow) {
                            isDialogShow = true;
                            String pageInfo = getPageInfo();

                            AlertDialog dialog = new AlertDialog.Builder(currentActivity)
                                    .setTitle("页面结果")
                                    .setMessage(pageInfo)
                                    .setCancelable(true)
                                    .create();
                            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    isDialogShow = false;
                                }
                            });
                            dialog.show();

                            // 震动
                            Vibrator vibrator = (Vibrator) currentActivity.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            }, sensorManager.getDefaultSensor(SensorManager.SENSOR_STATUS_ACCURACY_LOW), 70);
        } catch (Exception e) {
            ExceptionManager.handle(e);
        }
    }

    // 获取Activity信息
    private static String getPageInfo() {
        if (null == currentActivity) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Activity：\n" + currentActivity.getClass().getSimpleName());
        // 获取Activity中的Fragment信息
        if (currentActivity instanceof AppCompatActivity) {
            FragmentManager supportFragmentManager = ((AppCompatActivity) currentActivity).getSupportFragmentManager();
            getFragmentInfo(1, sb, supportFragmentManager);
        }
        return sb.toString();
    }

    // 获取Fragment信息
    private static void getFragmentInfo(int level, StringBuilder sb, FragmentManager supportFragmentManager) {
        if (null != supportFragmentManager) {
            List<Fragment> fragments = supportFragmentManager.getFragments();
            if (null != fragments && fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    if (null != fragment) {
                        if (level > 1) sb.append("\n子->Frg：");
                        else sb.append("\n父->Frg：");
                        sb.append("\n").append(fragment.getClass().getSimpleName());
                        // 获取子Fragment
                        getFragmentInfo(level + 1, sb, fragment.getChildFragmentManager());
                    }
                }
            }
        }
    }


}
