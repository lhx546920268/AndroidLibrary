package com.lhx.library.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */

public class DateUtil {

    //yyyy-MM-dd HH:mm:ss
    public static final String DateFormatYMdHms = "yyyy-MM-dd HH:mm:ss";

    //yyyy-MM-dd HH:mm
    public static final String DateFormatYMdHm = "yyyy-MM-dd HH:mm";

    //yyyy-MM-dd
    public static final String DateFormatYMd = "yyyy-MM-dd";

    //使用单例， 提升效率
    static final SimpleDateFormat mYMdHmsDateFormat = new SimpleDateFormat(DateFormatYMdHms, Locale.getDefault());
    static final SimpleDateFormat mYMdHmDateFormat = new SimpleDateFormat(DateFormatYMdHm, Locale.getDefault());
    static final SimpleDateFormat mYMdDateFormat = new SimpleDateFormat(DateFormatYMd, Locale.getDefault());


    /**
     * 把yyyy-MM-dd HH:mm:ss时间转成给定格式
     * @param time 要转的时间字符串
     * @param targetFromat 目标格式
     * @return 格式化的时间
     */
    public static String formatTime(String time, String targetFromat){
        try {
            return formatDate(mYMdHmsDateFormat.parse(time), targetFromat);
        }catch (ParseException e){
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 把时间戳转成给定格式
     * @param timestamp 要转的时间戳
     * @param targetFromat 目标格式
     * @return 格式化的时间
     */
    public static String formatTime(long timestamp, String targetFromat){
        return formatDate(new Date(timestamp), targetFromat);
    }

    /**
     * 把时间转成给定格式
     * @param date 要转的时间
     * @param targetFromat 目标格式
     * @return 格式化的时间
     */
    public static String formatDate(Date date, String targetFromat){

        if(mYMdDateFormat.equals(targetFromat)){
            return mYMdDateFormat.format(date);
        }else if(mYMdHmDateFormat.equals(targetFromat)){
            return mYMdHmDateFormat.format(date);
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(targetFromat, Locale.getDefault());
            return dateFormat.format(date);
        }
    }

    /**
     * 获取当前时间
     * @param format 时间格式
     */
    public static String getCurrentTime(String format){
        return formatDate(new Date(), format);
    }
}
