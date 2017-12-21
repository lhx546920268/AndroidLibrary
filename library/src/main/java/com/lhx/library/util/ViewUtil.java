package com.lhx.library.util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;

/**
 * 视图工具类
 */

public class ViewUtil {

    /**
     * 设置背景 兼容api level
     * @param backgroundDrawable 背景
     * @param view 要设置背景的view
     */
    public static void setBackground(Drawable backgroundDrawable, View view){

        if(view != null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                view.setBackground(backgroundDrawable);
            }else {
                view.setBackgroundDrawable(backgroundDrawable);
            }
        }
    }

    /**设置view大小 dp
     * @param view 要设置的view
     * @param width 宽度
     * @param height 高度
     */
    public static void setViewSize(View view, float width, float height){

        if(view != null){
            ViewGroup.LayoutParams params = view.getLayoutParams();
            if(params != null){
                params.width = SizeUtil.pxFormDip(width, view.getContext());
                params.height = SizeUtil.pxFormDip(height, view.getContext());
                view.setLayoutParams(params);
            }
        }
    }

    public static int getViewVisiableHeight(View view, int containerHeight){
        if(view == null)
            return 0;

        int top = view.getTop();
        int height = view.getHeight();
        return Math.min(containerHeight - Math.abs(top), height);
    }
}
