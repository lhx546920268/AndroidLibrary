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

    ///年月日时分秒 {@link #formatMs}
    public static final int Year = 0;
    public static final int Month = 1;
    public static final int Day = 2;
    public static final int Hour = 3;
    public static final int Minutes = 4;
    public static final int Seconds = 5;


    //使用单例， 提升效率
    public static final SimpleDateFormat mYMdHmsDateFormat = new SimpleDateFormat(DateFormatYMdHms, Locale.getDefault
            ());
    public static final SimpleDateFormat mYMdHmDateFormat = new SimpleDateFormat(DateFormatYMdHm, Locale.getDefault());
    public static final SimpleDateFormat mYMdDateFormat = new SimpleDateFormat(DateFormatYMd, Locale.getDefault());


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
     * 时间转成给定格式
     * @param time 要转的时间字符串
     * @param timeFormat 当前时间格式
     * @param targetFormat 目标格式
     * @return 格式化的时间
     */
    public static String formatTime(String time, String timeFormat, String targetFormat){
        return formatDate(parseTime(time, timeFormat), targetFormat);
    }

    /**
     * 把时间戳转成给定格式
     * @param timestamp 要转的时间戳
     * @param targetFormat 目标格式
     * @return 格式化的时间
     */
    public static String formatTime(long timestamp, String targetFormat){
        return formatDate(new Date(getMicroTimestamp(timestamp)), targetFormat);
    }

    /**
     * 把时间转成给定格式
     * @param date 要转的时间
     * @param targetFormat 目标格式
     * @return 格式化的时间
     */
    public static String formatDate(Date date, String targetFormat){

        if(DateFormatYMd.equals(targetFormat)){
            return mYMdDateFormat.format(date);
        }else if(DateFormatYMdHm.equals(targetFormat)){
            return mYMdHmDateFormat.format(date);
        }else {
            SimpleDateFormat dateFormat = new SimpleDateFormat(targetFormat, Locale.getDefault());
            return dateFormat.format(date);
        }
    }

    /**
     * 获取时间戳
     * @param time 时间字符串
     * @param format 时间格式
     * @return 时间戳
     */
    public static long timestampFromTime(String time, String format){
        Date date = parseTime(time, format);
        if(date != null){
            return date.getTime();
        }
        return 0;
    }

    /**
     * 获取当前时间
     * @param format 时间格式
     */
    public static String getCurrentTime(String format){
        return formatDate(new Date(), format);
    }


    @Deprecated
    public static Date praseTime(String time, String format){
        return parseTime(time, format);
    }

    /**
     * 解析时间
     * @param time 时间
     * @param format 格式
     * @return date
     */
    public static Date parseTime(String time, String format){
        try {
            if(DateFormatYMd.equals(format)){
                return mYMdDateFormat.parse(time);
            }else if(DateFormatYMdHm.equals(format)){
                return mYMdHmDateFormat.parse(time);
            }else if(DateFormatYMdHms.equals(format)){
                return mYMdHmsDateFormat.parse(time);
            }
            else {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
                return dateFormat.parse(time);
            }

        }catch (ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 格式化毫秒
     * @param ms 毫秒
     * @param type 返回的类型 {@link #Year}
     * @return
     */
    public static String formatMs(long ms, int type){

        long seconds = ms / 1000;
        if(type == Seconds){
            return String.valueOf(seconds);
        }

        long minutes = seconds / 60;
        if(type == Minutes){
            return String.valueOf(minutes);
        }

        long hour = minutes / 60;
        if(type == Hour){
            return String.valueOf(hour);
        }

        long day = hour / 24;
        if(type == Day){
            return String.valueOf(day);
        }

        long month = day / 30;
        if(type == Month){
            return String.valueOf(month);
        }

        long year = month / 12;
        if(type == Year){
            return String.valueOf(year);
        }

        return String.valueOf(ms);
    }

    /**
     * 格式化秒
     * @param seconds 要格式化的秒
     * @return 00:00:00
     */
    public static String formatSeconds(int seconds){
        int result = seconds / 60;
        int second = seconds % 60;
        int minute = result % 60;
        int hour = result / 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
    }

    /**
     * 获取符合java的时间戳
     * @param timestamp 时间戳
     * @return 13位时间戳
     */
    public static long getMicroTimestamp(long timestamp){
        if(String.valueOf(timestamp).length() == 13){
            return timestamp;
        }

        return timestamp * 1000;
    }

    public static long getPHPTimestamp(long timestamp){
        if(String.valueOf(timestamp).length() == 10){
            return timestamp;
        }

        return timestamp / 1000;
    }
}
