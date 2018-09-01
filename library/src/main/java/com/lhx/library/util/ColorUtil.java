package com.lhx.library.util;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;

/**
 * 颜色工具类
 */

public class ColorUtil {

    /**
     * 获取白色的百分比颜色 可用于获取某种程度的灰色 或黑色
     * @param percent 白色百分比 0 ~ 1.0
     * @param alpha 透明度 0 ~ 1.0
     * @return 颜色
     */
    public static int whitePercentColor(float percent, float alpha){

        int v = (int)(0xff * percent);
        int a = (int)(0xff * alpha);

        return (a << 24) | (v << 16) | (v << 8) | v;
    }

    /**
     * 生成带透明度的颜色
     * @param colorString 颜色16进制 不带透明度
     * @param alpha 透明度 0-1.0
     * @return 颜色值
     */
    public static int colorWithAlpha(@NonNull String colorString, float alpha){

        if(colorString.length() == 7){
            colorString = "#" + hexFromFloat(alpha) + colorString.replaceFirst("#", "");
            return Color.parseColor(colorString);
        }

        return Color.parseColor(colorString);
    }

    //小数转16进制字符串
    public static String hexFromFloat(float value){
        if(value < 0){
            value = 0;
        }else if(value > 1.0f){
            value = 1.0f;
        }

        int a = (int)(0xff * value);
        return hexFromInt(a / 16) + hexFromInt(a % 16);
    }

    private static String hexFromInt(int value){
        switch (value){
            case 10 :
                return "A";
            case 11 :
                return "B";
            case 12 :
                return "C";
            case 13 :
                return "D";
            case 14 :
                return "E";
            case 15 :
                return "F";
        }
        return String.valueOf(value);
    }
}
