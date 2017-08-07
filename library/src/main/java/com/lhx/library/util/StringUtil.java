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
import java.util.Random;
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

    /**
     * 解析字符串为长整数字
     * @param string 要解析的字符串
     * @return 长整数字
     */
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
}
