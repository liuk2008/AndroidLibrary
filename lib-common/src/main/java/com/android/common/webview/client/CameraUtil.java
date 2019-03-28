package com.android.common.webview.client;


import android.hardware.Camera;

/**
 * Created by Administrator on 2018/3/21.
 */

public class CameraUtil {

    public static boolean isCameraUsed() {

        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5。MX5通过Camera.open()拿到的Camera对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

}
