package com.lhx.library;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

import com.lhx.library.image.ImageLoaderUtil;

/**
 * app信息
 */

public class App {

    //默认导航栏颜色
    public static int NavigationBarBackgroundColor = Color.WHITE;

    //导航栏高度
    public static float NavigationBarHeightDip = 45.0f;

    //导航栏阴影颜色
    public static int NavigationBarShadowColor = Color.LTGRAY;

    //导航栏tint颜色
    public static int NavigationBarTintColor = Color.parseColor("#009784");

    //导航栏标题大小
    public static float NavigationBarTitleTextSize = 17.0f;

    //导航栏左右按钮字体大小
    public static float NavigatonBarButtonTextSize = 13.0f;

    //导航栏返回按钮图标 优先使用
    public static @DrawableRes int NavigationBarBackButtonIcon = 0;

    //http分页请求第一页
    public static int HttpFirstPage = 1;

    //图片 加载中显示的
    public static @DrawableRes int ImagePlaceHolder = 0;
    public static @DrawableRes int ImagePlaceHolderCircle = 0;


    public static void init(Context context){
        ImageLoaderUtil.initialize(context);
    }
}
