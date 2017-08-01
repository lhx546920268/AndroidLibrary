package com.lhx.library;

import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;

/**
 * app信息
 */

public class App {

    //默认导航栏颜色
    public static int NavigationBarBackgroundColor = Color.WHITE;

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
}
