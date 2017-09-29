package com.lhx.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串功能类
 */

public class StringUtil {

    /**
     * 获取一段随机数字
     * @param length 数字长度
     * @return 随机数
     */
    public static String getRandomNumber(int length){

        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i < length;i ++){
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }

    /**
     * 解析字符串为整型
     * @param string 要解析的字符串
     * @return 整型
     */
    public static int parseInt(String string){
        int value = 0;
        try {
            value = Integer.parseInt(string);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        return value;
    }

    public static long parseLong(String string){
        long value = 0;
        if(!isEmpty(string)){
            try {
                value = Long.parseLong(string);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return value;
    }

    public static float parseFloat(String string){
        float value = 0;
        if(!isEmpty(string)){
            try {
                value = Float.parseFloat(string);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return value;
    }

    public static double parseDouble(String string){
        double value = 0;
        if(!isEmpty(string)){
            try {
                value = Double.parseDouble(string);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 判断字符串是否为空
     * @param string 要判断的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String string){
        return string == null || string.length() == 0 || "null".equals(string);
    }

    public static String stringFromBytes(byte[] bytes){
        return stringFromBytes(bytes, "utf-8");
    }

    /**
     * 通过字节生成字符串
     * @param bytes 字节数组
     * @param charset 字符编码
     * @return 字符串
     */
    public static String stringFromBytes(byte[] bytes, String charset){
        if(bytes == null || bytes.length == 0)
            return "";

        try {
            return new String(bytes, 0, bytes.length, charset);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return "";
        }
    }

    public static int mesureTextHeight(CharSequence text, Context context, int textSize, int maxWidth){
        return mesureTextHeight(text, context, textSize, null, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f);
    }

    public static int mesureTextHeight(CharSequence text, TextPaint paint, int maxWidth){
        return mesureTextHeight(text, null, 0, paint, maxWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f);
    }

    /**
     * 计算文字高度
     * @param text 要计算的文字
     * @param context 上下文， 和 paint 必须传一个，传paint就可以不传 context了
     * @param textSize 字体大小，如果paint已传，会忽略这个
     * @param paint 可用{@link TextView#getPaint()}
     * @param maxWidth 显示文字的最大宽度 px
     * @param alignment 文字对齐方式
     * @param lineSpacingMultiplier 行距倍数 {@link TextView#getLineSpacingMultiplier()} default is 1.0f
     * @param lineSpacingExtra 行距额外高度 {@link TextView#getLineSpacingExtra()} default is 0.0f
     * @return 文字高度
     */
    public static int mesureTextHeight(CharSequence text, Context context, int textSize, TextPaint paint, int maxWidth,
                                       Layout.Alignment alignment, float lineSpacingMultiplier, float lineSpacingExtra){
        if(TextUtils.isEmpty(text))
            return 0;

        if(paint == null){
            if(context == null)
                throw new  NullPointerException("context or paint must pass one");

            Resources res = context.getResources();
            paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            paint.density = res.getDisplayMetrics().density;
            paint.setTextSize(textSize);
        }

        if(alignment == null){
            alignment = Layout.Alignment.ALIGN_NORMAL;
        }

        StaticLayout layout = new StaticLayout(text, paint, maxWidth, alignment, lineSpacingMultiplier,
                lineSpacingExtra, true);
        return layout.getHeight();
    }

    /**
     * 判断是否为纯字母
     */
    public static boolean isAlpha(String str){
        if(TextUtils.isEmpty(str))
            return false;
        return Pattern.compile("^[A-Za-z]+$").matcher(str).matches();
    }

    /**
     * 判断是否为字母或数字组合
     */
    public static boolean isAlphaNumber(String str){

        if(TextUtils.isEmpty(str))
            return false;
        return Pattern.compile("^[A-Za-z0-9]+$").matcher(str).matches();
    }

    /**
     * 判断是否为纯数字
     */
    public static boolean isNumber(String str){
        if(TextUtils.isEmpty(str))
            return false;
        return Pattern.compile("^[0-9]+$").matcher(str).matches();
    }

    /**
     * 判断是否为手机号
     *
     * @param mobile 手机号
     * @return 返回true则是手机号
     */
    public static boolean isMoile(String mobile) {

        if (TextUtils.isEmpty(mobile))
            return false;
        return Pattern.compile("^1[3|4|5|7|8][0-9]\\d{8}$").matcher(mobile).matches();
    }

    /**
     * 判断是否为固话
     *
     * @param tel 固话
     * @return 返回true则是手机号
     */
    public static boolean isTelPhoneNumber(String tel) {

        if (TextUtils.isEmpty(tel))
            return false;
        return Pattern.compile("((\\d{12})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-" +
                "(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)|(\\d{11})").matcher(tel)
                .matches();
    }

    /**
     * 是否是邮箱
     */
    public static boolean isEmail(String email){
        if (TextUtils.isEmpty(email))
            return false;
        return Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}").matcher(email)
                .matches();
    }

    /*********************************** 身份证验证开始 ****************************************/
    /**
     * 身份证号码验证 1、号码的结构 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，
     * 八位数字出生日期码，三位数字顺序码和一位数字校验码。 2、地址码(前六位数）
     * 表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。 3、出生日期码（第七位至十四位）
     * 表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。 4、顺序码（第十五位至十七位）
     * 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。 5、校验码（第十八位数）
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4
     * 2 （2）计算模 Y = mod(S, 11) （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1 0
     * X 9 8 7 6 5 4 3 2
     */

    /**
     * 身份证的有效验证
     * @param str 身份证号
     * @return 是否是有效身份证
     */
    public static boolean isIdCard(String str) {

        if(isEmpty(str))
            return false;

        // 号码的长度 15位或18位
        if (str.length() != 15 && str.length() != 18) {
            return false;
        }

        String Ai = "";

        //数字 除最后以为都为数字
        if (str.length() == 18) {
            Ai = str.substring(0, 17);
        } else if (str.length() == 15) {
            Ai = str.substring(0, 6) + "19" + str.substring(6, 15);
        }

        //身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字
        if (!isNumeric(Ai)) {
            return false;
        }

        //出生年月是否有效
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份

        //身份证生日无效
        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            return false;
        }

        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = DateUtil.mYMdDateFormat;
        try {

            //身份证生日不在有效范围
            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                    || (gc.getTime().getTime() - s.parse(
                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
                return false;
            }

            //身份证月份无效
            if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
                return false;
            }

            //身份证日期无效
            if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
                return false;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return false;
        }

        //地区码时候有效
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            //身份证地区编码错误
            return false;
        }

        //判断最后一位的值
        int TotalmulAiWi = 0;
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };

        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi + Integer.parseInt(String.valueOf(Ai.charAt(i))) * Integer.parseInt(Wi[i]);
        }

        int modValue = TotalmulAiWi % 11;

        String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;


        if (str.length() == 18) {
            if (!Ai.equals(str)) {
                //身份证无效，不是合法的身份证号码
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    /**
     * 功能：设置地区编码
     *
     * @return Hashtable 对象
     */
    @SuppressWarnings("unchecked")
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**
     * 功能：判断字符串是否为数字
     * @param str xx
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 功能：判断字符串是否为日期格式
     *
     * @param str xx
     * @return
     */
    public static boolean isDate(String str) {
        Pattern pattern = Pattern
                .compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(str);
        return m.matches();
    }

    /*********************************** 身份证验证结束 ****************************************/
}
