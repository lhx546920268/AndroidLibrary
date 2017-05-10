package com.lhx.library.util;

import android.content.Context;
import android.util.TypedValue;

///尺寸 功能类
public class SizeUtil{

    /**
     * 将dip转换为px
     * @param dipValue 要转换的dip值
     * @return px值
     */
    public static int pxFormDip(float dipValue, Context context) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources()
                .getDisplayMetrics());
    }

    /**
     * 将px转换为dip
     * @param pxValue 要转换的px值
     * @return dp值
     */
    public static float dipFromPx(int pxValue, Context context) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    /**
     * sp 转 px
     * @param spValue 要转换的sp值
     * @return sp值
     */
    public static int pxFromSp(float spValue, Context context) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值
     * @param pxValue 要转换的px值
     * @return sp值
     */
    public static float spFromPx(int pxValue, Context context) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale + 0.5f;
    }
}
