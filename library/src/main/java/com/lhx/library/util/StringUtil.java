package com.lhx.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Random;

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
        try {
            value = Long.parseLong(string);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        return value;
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
}
