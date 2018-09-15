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
        if(dipValue == 0)
            return 0;
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources()
                .getDisplayMetrics());
    }

    /**
     * 将px转换为dip
     * @param pxValue 要转换的px值
     * @return dp值
     */
    public static float dipFromPx(int pxValue, Context context) {
        if(pxValue == 0)
            return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    /**
     * sp 转 px
     * @param spValue 要转换的sp值
     * @return sp值
     */
    public static int pxFromSp(float spValue, Context context) {
        if(spValue == 0)
            return 0;
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值
     * @param pxValue 要转换的px值
     * @return sp值
     */
    public static float spFromPx(int pxValue, Context context) {
        if(pxValue == 0)
            return 0;
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return pxValue / fontScale + 0.5f;
    }


    /**
     * 获取屏幕宽度
     * @return 宽度
     */
    public static int getWindowWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     * @return 高度
     */
    public static int getWindowHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getStatusBarHeight(Context context){
        int height = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            height = context.getResources().getDimensionPixelSize(resId);
        }

        if(height == 0){
            height = SizeUtil.pxFormDip(20, context);
        }

        return height;
    }
}
