package com.android.common.utils.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/10/31.
 */

public class BitmapUtils {

    /**
     * 质量压缩图片
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 90;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;// 每次都减少10
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 尺寸压缩图片
     */
    public static ByteArrayOutputStream compressPicture(Context context, String picPath) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picPath, op);
        // 原图片的尺寸
        int picWidth = op.outWidth;
        int picHeight = op.outHeight;
        // 获取屏幕尺寸
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display defaultDisplay = windowManager.getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        // 计算压缩比例
        float size = 1.0f;
        if (picWidth > height && picWidth > width) {
            size = picWidth * 1.0f / width;
        } else if (picWidth < height && picHeight > height) {
            size = picHeight * 1.0f / height;
        }
        // 设置缩放比例,这个数字越大,图片大小越小.
        op.inSampleSize = (int) size;
        op.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(picPath, op); // 缩放后的Bitmap
        // 将图片转换为字节流
        ByteArrayOutputStream baso = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baso);
        recycleBitmap(bitmap);
        return baso;
    }

    // 释放资源
    private static void recycleBitmap(Bitmap... bitmaps) {
        if (bitmaps == null) {
            return;
        }
        for (Bitmap bm : bitmaps) {
            if (null != bm && !bm.isRecycled()) {
                bm.recycle();
            }
        }
    }

    /**
     * 资源文件转换为bitmap
     * <p>
     * 在onCreate方法中加入下面两行代码
     * getWindow().setFormat(PixelFormat.RGBX_8888);
     * BitmapFactory.setDefaultConfig(Bitmap.Config.ARGB_8888);
     */
    public static Bitmap ResToBitmap(Context context, int resId) {
        View view = View.inflate(context, resId, null);
        //调用下面这个方法非常重要，如果没有调用这个方法，得到的bitmap为null
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //这个方法也非常重要，设置布局的尺寸和位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        //把view中的内容绘制在画布上
        view.draw(canvas);
        return bitmap;
    }

    // bitmap 转换为byte[]
    public static byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baso = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baso);
        return baso.toByteArray();
    }

}
