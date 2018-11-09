package com.lhx.library;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.View;

import com.lhx.library.image.ImageLoaderUtil;
import com.lhx.library.loading.LoadingView;
import com.lhx.library.refresh.RefreshHeader;

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

    //回到顶部
    public static @DrawableRes int BackToTopIcon = 0;

    //自定义的loading视图
    public static Class<? extends LoadingView> pageLoadingViewClass = null;
    public static Class<? extends View> loadViewClass = null;

    //自定义下拉刷新头部 要实现 PtrUIHandler
    public static Class<? extends RefreshHeader> refreshHeaderClass = null;


    public static void init(Context context){
        ImageLoaderUtil.initialize(context);
    }
}
