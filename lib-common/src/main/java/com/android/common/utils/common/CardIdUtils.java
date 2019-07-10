package com.android.common.utils.common;

import android.content.Context;
import android.text.TextUtils;

import java.util.Calendar;

/**
 * 身份证号前台校验规则：
 * 1. 身份证号18位组成，从第1位到第17位是由数字组成，第18号可以是数字或者字母X
 * 2. 校验出身年月在 1900与当前时间之间
 * 3. 校验18位身份证号的校验码算法是否正确
 * 4. 校验前两位代表的省份是否存在
 * Created by Administrator on 2017/7/13.
 */

public class CardIdUtils {

    private static final String TAG = "CardIdUtils";

    private static final String regex = "(\\d{17}[0-9a-zA-X])";
    /**
     * 省、直辖市代码表
     */
    private static final String cityCode[] = {"11", "12", "13", "14", "15",
            "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41",
            "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61",
            "62", "63", "64", "65", "71", "81", "82"};

    public static boolean isCardId(Context context, String cardId) {

        if (TextUtils.isEmpty(cardId)) {
            ToastUtils.getToast(context, "请输入18位身份证号码");
            return false;
        } else {
            if (checkCardId(cardId)) {
                return true;
            } else {
                ToastUtils.getToast(context, "身份证号不正确");
                return false;
            }
        }
    }

    public static boolean checkCardId(String cardId) {
        // 校验位数
        if (!cardId.matches(regex)) {
            LogUtils.logd(TAG, "位数错误");
            return false;
        }

        // 校验码
        char verifyChar = getVerifyChar(cardId);
        if (cardId.charAt(17) != verifyChar) {
            LogUtils.logd(TAG, "校验码错误");
            return false;
        }

        // 校验当前年份
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int year = Integer.valueOf(cardId.substring(6, 10));
        if (year < 1900 || year >= currentYear) {
            LogUtils.logd(TAG, "年份错误");
            return false;
        }

        // 校验省份
        String city = cardId.substring(0, 2);
        for (int i = 0; i < cityCode.length; i++) {
            if (city.equals(cityCode[i])) {
                break;
            }
            if (!city.equals(cityCode[i]) && i == (cityCode.length - 1)) {
                LogUtils.logd(TAG, "省份错误");
                return false;
            }
        }
        return true;
    }

    // 获取校验位
    private static char getVerifyChar(String idCardNumber) {
        char pszSrc[] = idCardNumber.toCharArray();
        int iS = 0;
        int iW[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char szVerCode[] = new char[]{'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        for (int i = 0; i < 17; i++) {
            iS += (int) (pszSrc[i] - '0') * iW[i];
        }
        int iY = iS % 11;
        return szVerCode[iY];
    }

}
