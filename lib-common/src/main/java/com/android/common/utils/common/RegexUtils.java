package com.android.common.utils.common;

import android.content.Context;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/7/14.
 */

public class RegexUtils {

    private static final String regex = "[\\u4E00-\\u9FA5]+";
    //    private static final String telRegex = "^1[3,4,5,7,8]\\d{9}$";
    private static final String telRegex = "^1\\d{10}$";
    //    private static final String pwdRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@#$%^&*,._-])[0-9a-zA-Z~!@#$%^&*,._-]+$";
    private static final String pwdRegex = "^[0-9a-zA-Z~!@#$%^&*,._-]+$";

    // 校验电话
    public static boolean isPhone(Context context, String phone, String msg) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showToast(context, msg);
            return false;
        } else if (!phone.matches(telRegex)) {
            ToastUtils.getToast(context, "手机号格式错误");
            return false;
        } else {
            return true;
        }
    }

    // 校验姓名
    public static boolean isUsername(Context context, String name) {
        if (TextUtils.isEmpty(name)) {
            ToastUtils.getToast(context, "请输入真实姓名");
            return false;
        } else if (!name.matches(regex)) {
            ToastUtils.getToast(context, "姓名只能为汉字");
            return false;
        } else if (name.length() > 7) {
            ToastUtils.getToast(context, "姓名最长为7位汉字");
            return false;
        } else {
            return true;
        }
    }

    public static boolean isContentEmpty(Context context, String content, String msg, int maxLength) {
        String regex = "^([\\u4E00-\\u9FA5]|[0-9a-zA-Z]|[\\n`~!@#$%^&*()-+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）【】‘；：”“’。，、？ ])+$";
        if (TextUtils.isEmpty(content)) {
            ToastUtils.getToast(context, msg);
            return false;
        }
        if (content.length() > maxLength) {
            ToastUtils.getToast(context, "内容最多可输入" + maxLength + "个字或字符");
            return false;
        }
        if (!content.matches(regex)) {
            ToastUtils.getToast(context, "输入的内容存在非法字符");
            return false;
        }
        return true;
    }

    // 校验内容
    public static boolean isContentLength(Context context, String content, String msg,
                                          int maxLength) {
        if (TextUtils.isEmpty(content)) {
            ToastUtils.getToast(context, msg);
            return false;
        } else
            try {
                int length = content.getBytes("gbk").length;
                if (length > maxLength) {
                    ToastUtils.getToast(context, "内容最多可输入" + (maxLength / 2) + "个汉字或" + maxLength + "个字符");
                    return false;
                } else {
                    return true;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
    }

    // 校验密码
    public static boolean isPwd(Context context, String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.showToast(context, "请输入密码");
            return false;
        } else if (pwd.length() > 20 || pwd.length() < 6) {
            ToastUtils.showToast(context, "请输入6-20位半角字符，建议数字、字母、符号组合");
            return false;
        } else if (!pwd.matches(pwdRegex)) {
            ToastUtils.showToast(context, "请输入6-20位半角字符，建议数字、字母、符号组合");
            return false;
        } else {
            return true;
        }
    }

}
