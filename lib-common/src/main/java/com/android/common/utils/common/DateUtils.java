package com.android.common.utils.common;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/8/11.
 */

public class DateUtils {

    private static SimpleDateFormat month = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取本地时间
     */
    public static String getNowTime() {
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    /**
     * 时间戳转换成字符串
     */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(d);
    }

    /**
     * 字符串转换成时间戳
     */
    public static long getStringToDate(String time) {
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    /**
     * 获取n月前月份
     */
    public static String getLastMonth(int i) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为当前时间
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - i); // 设置为上一个月
        date = calendar.getTime();
        return month.format(date);
    }

    public static void showDatePick(Context context) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = String.valueOf(monthOfYear + 1);
                String day = String.valueOf(dayOfMonth);
                if (monthOfYear + 1 < 10) {
                    month = "0" + month;
                }
                if (dayOfMonth < 10) {
                    day = "0" + dayOfMonth;
                }
                String date = year + "-" + month + "-" + day;
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    /**
     * 1小时之内的数据显示*分钟前，小于一分钟显示刚刚，大于1小时小于一天的数据显示*小时前，大于一天的显示*天前
     */
    public static String showTime(String time) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = df.parse(time);
        long currentTimeMillis = System.currentTimeMillis();

        long timeMillis = date.getTime();
        long result = Math.abs(currentTimeMillis - timeMillis);

        if (result < 60 * 1000) {// 一分钟内
            return "刚刚";
        } else if (result >= 60 * 1000 && result < 60 * 60 * 1000) {// 一小时内
            long seconds = result / (60 * 1000);
            return seconds + "分钟前";
        } else if (result >= 60 * 60 * 1000 && result < 24 * 60 * 60 * 1000) {// 一天内
            long seconds = result / (60 * 60 * 1000);
            return seconds + "小时前";
        } else {// 一天前
            long seconds = result / (24 * 60 * 60 * 1000);
            return seconds + "天前";
        }
    }


    /**
     * 将long类型的时间转为01:33:22这样的格式
     */
    public static String formatVideoDuration(long duration) {
        int HOUR = 60 * 60 * 1000;//小时对应的毫秒数
        int MINUTE = 60 * 1000;//分钟对应的毫秒数
        int SECOND = 1000;//

        //1.先算出多少小时，然后拿剩余的时间算出分钟
        int hour = (int) (duration / HOUR);//得到多少小时
        long remainTime = duration % HOUR;//得到算完小时后剩余的时间
        //2.算出多少分钟，然后拿剩余的时间算出多少秒
        int minute = (int) (remainTime / MINUTE);//得到多少分钟
        remainTime = remainTime % MINUTE;//得打算完分钟后剩余的时间
        //3.算出多少秒
        int second = (int) (remainTime / SECOND);//

        if (hour == 0) {
            //显示33:22这样格式
            return String.format("%02d:%02d", minute, second);
        } else {
            //显示01:33:22
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
    }

}
