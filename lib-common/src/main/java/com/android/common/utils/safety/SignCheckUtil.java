package com.android.common.utils.safety;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.android.common.utils.common.LogUtils;

import java.security.MessageDigest;


/**
 * 获取指定已安装完整签名信息,包括MD5指纹
 */
public class SignCheckUtil {

    private static final String TAG = "SignCheckUtil";

    public static Boolean isLegal(Context context) {
        String value = "9C0CE93308B8DC35ADD10274873B6DFD";
        LogUtils.log(TAG, "默认值：" + stringToAscii(value));
        String sign = getSignInfo(context);
        LogUtils.log(TAG, "Ascii：" + stringToAscii(sign));
        return true;
    }

    private static String getSignInfo(Context context) {
        String certMD5 = "";
        try {
            // 获取签名文件信息
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature signature = signs[0];
            // 获取签名文件的MD5值
            byte[] signByte = signature.toByteArray();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 得到签名文件的的字节数组
            messageDigest.update(signByte);
            // 得到十六个十进制值
            byte[] resultByteArray = messageDigest.digest();
            // 转换成十六进制的MD5值
            certMD5 = byteArrayToHex(resultByteArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return certMD5;
    }

    private static String byteArrayToHex(byte[] byteArray) {
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    private static String stringToAscii(String value) {
        StringBuilder sb = new StringBuilder();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i != chars.length - 1) {
                sb.append((int) chars[i]).append(",");
            } else {
                sb.append((int) chars[i]);
            }
        }
        return sb.toString();
    }

}
