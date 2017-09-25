package com.lhx.library;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

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
    public static int NavigationBarTintColor = Color.BLACK;

    //导航栏标题大小
    public static float NavigationBarTitleTextSize = 17.0f;

    //导航栏左右按钮字体大小
    public static float NavigatonBarButtonTextSize = 13.0f;

    //导航栏返回按钮图标 优先使用
    public static @DrawableRes int NavigationBarBackButtonIcon = 0;

    //导航栏返回按钮文字
    public static String NavigationBarBackButtonTitle = "返回";

    //http分页请求第一页
    public static int HttpFirstPage = 1;

    //长按钮圆角
    public static int LongButtonCornerRadius = 20;

    //web进度条颜色
    public static @ColorInt int WebProgressColor = Color.parseColor("#007AFF");

    //image 加载中的背景颜色
    public static @ColorInt int ImagePlaceHolderColor = Color.WHITE;

    public static @DrawableRes int ImagePlaceHolder = 0;
    public static @DrawableRes int ImagePlaceHolderCircle = 0;
}
