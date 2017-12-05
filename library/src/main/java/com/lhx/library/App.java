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
