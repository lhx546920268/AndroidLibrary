package com.lhx.library.drawable;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 *  drawable 工具类
 */

public class DrawableUtil {

    /**
     * 获取着色的 drawable
     * @param drawable 要着色的
     * @param tintColor 对应颜色
     * @return 着色后的drawable
     */
    public static Drawable getTintDrawable(Drawable drawable, @ColorInt int tintColor){

        Drawable wrapDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapDrawable, tintColor);
        return wrapDrawable;
    }

    public static Drawable getTintListDrawable(Drawable drawable, ColorStateList colorStateList){

        Drawable wrapDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrapDrawable, colorStateList);
        return wrapDrawable;
    }
}
