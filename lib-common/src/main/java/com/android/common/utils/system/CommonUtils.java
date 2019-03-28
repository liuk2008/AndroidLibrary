package com.android.common.utils.system;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.view.View;
import android.view.Window;

import java.util.Random;


/**
 * Created by liuk on 2018/8/22 0022.
 */

public class CommonUtils {


    /**
     * 获取strings配置文件中字符串资源
     */
    public static String getString(Context context, int resId) {

        return context.getString(resId);

    }

    /**
     * 获取drawable中图片资源
     */
    public static Drawable getDrawable(Context context, int id) {

        return context.getResources().getDrawable(id);
    }

    /**
     * 获取strings配置文件中的字符串资源，
     */
    public static String[] getStringArray(Context context, int id) {
        return context.getResources().getStringArray(id);
    }

    /**
     * 生成漂亮的颜色
     */
    public static int generateColor() {
        //颜色的范围是0-255，为使生成的颜色不至于太暗或太亮，所以取中间的值
        Random random = new Random();
        int red = random.nextInt(150) + 50;//50-199
        int green = random.nextInt(150) + 50;
        int blue = random.nextInt(150) + 50;
        return Color.rgb(red, green, blue);//使用rgb混合出一种新的颜色
    }


    /**
     * 生成圆角图片
     */
    public static GradientDrawable generateDrawable(int argb) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);//设置矩形
        drawable.setCornerRadius(5);//设置圆角半径
        drawable.setColor(argb);//设置颜色
        return drawable;
    }

    /**
     * 动态生成Selector
     */
    public static StateListDrawable generateSelector(Drawable normal, Drawable pressed) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);//添加按下的图片
        drawable.addState(new int[]{}, normal);
        return drawable;
    }

    /**
     * 设置状态栏
     * window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
     * <p>
     * 隐藏系统状态栏
     * <p>
     * 在布局文件中配置
     * android:clipToPadding="true"
     * android:fitsSystemWindows="true"
     */
    public void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
                Window window = activity.getWindow();
                View decorView = window.getDecorView();
                //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                window.setStatusBarColor(Color.TRANSPARENT);

            }
        }
    }

}
