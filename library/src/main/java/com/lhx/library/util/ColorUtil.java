package com.lhx.library.util;

import android.content.res.ColorStateList;

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
}
